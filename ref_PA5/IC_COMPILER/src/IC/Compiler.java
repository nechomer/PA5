package IC;

import IC.AST.ICClass;
import IC.AST.PrettyPrinter2;
import IC.AST.Program;
import IC.Parser.LibraryParser;
import IC.Parser.Parser;
import IC.SemanticChecks2.SemanticChecker;
import IC.SemanticChecks2.TableBuilderVisitor;
import IC.SemanticChecks2.TypeCheckingVisitor;
import IC.SymbolTable.GlobalSymbolTable;
import IC.Type.TypeTable;
import IC.asm.AssemblingVisitor;
import IC.asm.Info;
import IC.asm.MethodLayout;
import IC.asm.MethodLayoutBuildingVisitor;
import IC.lir.LirInstruction;
import IC.lir.TranslatingVisitor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java_cup.runtime.Symbol;

public class Compiler {

    // java IC.Compiler <file.ic> [ -L</path/to/libic.sig> | -print-ast | -dump-symtab]
    public static void main(String[] args) {
        String icFile = null;
        String libraryFile = null;
        boolean printAST = false, dumpSymTab = false, printLir = false;
        FileInputStream input = null;

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-print-ast")) {
                printAST = true;
            } else if (args[i].equals("-dump-symtab")) {
                dumpSymTab = true;
            } else if (args[i].equals("-print-lir")) {
                printLir = true;
            } else if (args[i].equals("-opt-asm")) {
                // Right now, do nothing
            } else if (args[i].startsWith("-L")) {
                libraryFile = args[i].substring(2);
            } else if (icFile == null) {
                icFile = args[i];
            } else {
                System.out.println("Bad command line option! " + args[i]);
                System.out.println("Aborting...");
                return;
            }
        }

        if (icFile == null) {
            System.out.println("No IC file specified. Stop.");
            return;
        } else if (!icFile.endsWith("ic")) {
            System.out.println("The file should be an ic filem, but it's name "
                    + "does not end with IC");
            return;
        }

        ICClass lib = null;
        Program pr = null;

        try {
            if (libraryFile != null) {
                input = new FileInputStream(libraryFile);

                LibraryParser p = new LibraryParser(new IC.Parser.Lexer(input)) {

                    @Override
                    public void syntax_error(Symbol cur_token) {
                        // This case should never happen
                        if (!(cur_token instanceof IC.Parser.Token)) {
                            super.syntax_error(cur_token);
                        } else {
                            IC.Parser.ErrorReporting.syntax_error(cur_token);
                        }

                    }
                };
                lib = (ICClass) p.parse().value;

                input.close();
                input = null;
            }

            input = new FileInputStream(icFile);

            Parser p = new Parser(new IC.Parser.Lexer(input)) {

                @Override
                public void syntax_error(Symbol cur_token) {
                    // This case should never happen
                    if (!(cur_token instanceof IC.Parser.Token)) {
                        super.syntax_error(cur_token);
                    } else {
                        IC.Parser.ErrorReporting.syntax_error(cur_token);
                    }

                }
            };

            pr = (Program) p.parse().value;

            input.close();


            if (lib != null) {
                if (!((ICClass) lib).getName().equals("Library")) {
                    throw new SemanticalError("The library class should be called \"Library\"!", 0);
                }
                pr.getClasses().add(0, lib);
            }

            SemanticChecker sc = new SemanticChecker();
            sc.visit(pr);

            TableBuilderVisitor tbv = new TableBuilderVisitor();
            GlobalSymbolTable gst = (GlobalSymbolTable) tbv.visit(pr);
            TypeCheckingVisitor tcv = new TypeCheckingVisitor(tbv.getClassSyms());
            tcv.visit(pr);

            TranslatingVisitor lirTranslator = new TranslatingVisitor(tcv);
            List<LirInstruction> lirProgram = (List<LirInstruction>) lirTranslator.visit(pr);

            Info info = new Info(tbv.getClassSyms());
            MethodLayoutBuildingVisitor mlbv = new MethodLayoutBuildingVisitor(info);
            Map <String, MethodLayout> offsets =(Map<String, MethodLayout>) mlbv.build(lirProgram);
            AssemblingVisitor av = new AssemblingVisitor(offsets, icFile);
            av.translate(lirProgram);


            if (printAST) {
                PrettyPrinter2 pt = new PrettyPrinter2(icFile, tcv);
                System.out.println(pt.visit(pr));
            }

            if (dumpSymTab) {
                gst.print();
                System.out.println();
                TypeTable.print();
            }


            if (printLir) { //write to file
                String lirFile = icFile.substring(0, icFile.lastIndexOf(".ic") + 1) + "lir";
                FileWriter fw = new FileWriter(lirFile);
                for (LirInstruction instruction : lirProgram) {
                    //out.write(instruction + "\n");
                    fw.write(instruction.toString() + '\n');
//                    System.out.println(instruction.toString());
                }
                fw.close();
            }

            /* Finally, print the assembler */
            String asmFile = icFile.substring(0, icFile.lastIndexOf(".ic") + 1) + "s";
            FileWriter fw = new FileWriter(asmFile);
            fw.write(av.getProgramString());
            fw.close();


        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Couldn't find the file specified. Stop.");
            return;
        } catch (SemanticalError ex) {
            System.out.println("semantic error at line " + ex.line + ": " + ex.getMessage());
            //ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("An exception occured. Aborting...");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                System.out.println("Could not close the file input stream");
                System.out.println(ex.getMessage());
            }
        }


    }
}
