package IC.Parser;

import java.math.BigDecimal;

%%

%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError
%cup

%eofval{
        /* Warnings for things which should be closed before the end of file... */
        if (yystate () == QUOTES || yystate () == QUOTES_END)
            throw new LexicalError ("Unterminated string - reached end of file!");
        else if (yystate () == COMMENTS2) /* A slash star comment */
            throw new LexicalError ("Unterminated comment - reached end of file!");
  	else
            return new Token(sym.EOF,yyline + 1);
%eofval}


WHITESPACE = " " | \n | \t 
LOWERCASE_LETTER = [a-z]
UPPERCASE_LETTER = [A-Z]
DIGIT = [0-9]
LETTER = {LOWERCASE_LETTER} | {UPPERCASE_LETTER}
IDENT = {LETTER} | {DIGIT} | "_"

/* Quoted strings may contain ascii characters from 32 (' ') to 126 ('~'), not
 * including the quotes ('"' 34) and backslash ('\' 92) characters
 */
QUOTED_CHAR=[\x20-\x21\x23-\x5b\x5d-\x7e]

/* The legal escape sequences inside strings are:
 * \" \\ \t \n
 */
QUOTED_ESCAPE=((\\\")|(\\\\)|(\\t)|(\\n))

%state COMMENTS2
%state QUOTES
%state QUOTES_END

%%

/* Note that all the token are initialized with yyline+1 because the teaching
 * assitant told me to do so, because we had an offset of 1 from the numbers in
 * your outputs.
 * He said it's a legitimate and perfect solution, and not a workaround!
 */
<YYINITIAL> {
 {WHITESPACE} {}

 "=" { return new Token(sym.ASSIGN, yyline + 1); }

 "boolean" { return new Token(sym.BOOLEAN, yyline + 1); }

 "break" { return new Token(sym.BREAK, yyline + 1); }

 "class" { return new Token(sym.CLASS, yyline + 1); }

 "," { return new Token(sym.COMMA, yyline + 1); }
 
 "//".* { }

 /* Start a multiline comment */
 "/*" { yybegin(COMMENTS2); }
 
 

 "continue" { return new Token(sym.CONTINUE, yyline + 1); }

 "/" { return new Token(sym.DIVIDE, yyline + 1); }

 "." { return new Token(sym.DOT, yyline + 1); }

 "==" { return new Token(sym.EQUAL, yyline + 1); }

 "extends" { return new Token(sym.EXTENDS, yyline + 1); }

 "else" { return new Token(sym.ELSE, yyline + 1); }

 "false" { return new Token(sym.FALSE, yyline + 1); }

 ">" { return new Token(sym.GT, yyline + 1); }

 ">=" { return new Token(sym.GTE, yyline + 1); }

 "if" { return new Token(sym.IF, yyline + 1); }

 "int" { return new Token(sym.INT, yyline + 1); }


 /* First we try a number which begins with zero, and HAS digits after the zero
  * If we succeed, it's an error
  */
 "0"{DIGIT}+ {
  throw new LexicalError (yyline, "Illegal leading zero(es) in - '" + yytext() + "'");
  }

 /* Otherwise, any sequence of digits which is of the right number limits is a valid
  * number. Note that we can't check the differences in positive and negative ranges
  * because we don't know if we had a minus before...
  */
 {DIGIT}+ { if(BigDecimal.valueOf(Integer.MIN_VALUE).abs().compareTo(new BigDecimal(yytext())) != -1) return new Token(sym.INTEGER, yytext(), yyline + 1);
          else throw new LexicalError (yyline, "Numbers is too big! " + yytext()); }

 /* Now recognize rubbish such as "123a4"
  * It's ok to have it after matching the regular number, since it's longer, so it will
  * still win if it has a match (it has the extra non-digit chars).
  */
 {DIGIT}({IDENT})+ {
  throw new LexicalError (yyline, "Illegal identifier - starts with digits! In - '" + yytext() + "'");
  }


 "&&" { return new Token(sym.LAND, yyline + 1); }

 "[" { return new Token(sym.LB, yyline + 1); }

 "(" { return new Token(sym.LP, yyline + 1); }

 "{" { return new Token(sym.LCBR, yyline + 1); }

 "length" { return new Token(sym.LENGTH, yyline + 1); }

 "new" { return new Token(sym.NEW, yyline + 1); }

 "!" { return new Token(sym.LNEG, yyline + 1); }

 "||" { return new Token(sym.LOR, yyline + 1); }

 "<" { return new Token(sym.LT, yyline + 1); }

 "<=" { return new Token(sym.LTE, yyline + 1); }

 "-" { return new Token(sym.MINUS, yyline + 1); }

 "%" { return new Token(sym.MOD, yyline + 1); }

 "*" { return new Token(sym.MULTIPLY, yyline + 1); }

 "!=" { return new Token(sym.NEQUAL, yyline + 1); }

 "null" { return new Token(sym.NULL, yyline + 1); }

 "+" { return new Token(sym.PLUS, yyline + 1); }

 "]" { return new Token(sym.RB, yyline + 1); }

 "}" { return new Token(sym.RCBR, yyline + 1); }

 "return" { return new Token(sym.RETURN, yyline + 1); }

 ")" { return new Token(sym.RP, yyline + 1); }

 ";" { return new Token(sym.SEMI, yyline + 1); }

 "static" { return new Token(sym.STATIC, yyline + 1); }

 "string" { return new Token(sym.STRING, yyline + 1); }
 

 /* Start marking a quoted string */
 "\"" { yybegin(QUOTES); }

 "this" { return new Token(sym.THIS, yyline + 1); }

 "true" { return new Token(sym.TRUE, yyline + 1); }

 "void" { return new Token(sym.VOID, yyline + 1); }

 "while" { return new Token(sym.WHILE, yyline + 1); }

 {UPPERCASE_LETTER}({IDENT})* { return new Token(sym.CLASS_ID, yytext(), yyline + 1); }

 {LOWERCASE_LETTER}({IDENT})* { return new Token(sym.ID, yytext(), yyline + 1); }

 . {
  throw new LexicalError (yyline + 1, "illegal character '" + yytext() + "'");
   }
 } /* end of YYINITIAL */

 /* Handle the end of multiline comments */
 <COMMENTS2> "*/" {yybegin(YYINITIAL); }
 <COMMENTS2> .|"\n" {}

/* Quoted strings may contain ascii characters from 32 (' ') to 126 ('~'), not
 * including the quotes ('"' 34) and backslash ('\' 92) characters, and also any
 * of the escape sequences \" \\ \t \n
 */
<QUOTES> ({QUOTED_CHAR}|{QUOTED_ESCAPE})* { yybegin(QUOTES_END); return new Token(sym.QUOTE,"\"" + yytext() + "\"", yyline + 1); }
/* Handle empty strings */
<QUOTES> \" { yybegin(YYINITIAL); return new Token(sym.QUOTE, "\"\"", yyline + 1); }
/* Handle illegal characters inside strings and/or untermintated strings
 * It's ok to put it here since the recognition of legal chars was before
 */
<QUOTES> "\n"  { throw new LexicalError (yyline + 1, "Illegal character inside quoted string '" + yytext() + "'"); }
<QUOTES> .  { throw new LexicalError (yyline + 1, "Illegal character inside quoted string '" + yytext() + "'"); }

/* This asserts that the string is finished */
<QUOTES_END> "\"" { yybegin(YYINITIAL); }

/* This solves errors about valid string letters, followed by an illegal string character */
<QUOTES_END> [^\"] { throw new LexicalError (yyline + 1, "Illegal character inside quoted string - '" + yytext() + "'"); }