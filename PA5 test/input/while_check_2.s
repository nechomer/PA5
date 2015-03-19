.title "while_check_2.ic"

# global declarations
.global __ic_main

# data section 
.data
	.align 4

	.int 40
str_err_null_ptr_ref:	.string	"Runtime Error: Null pointer dereference!"
	.int 41
str_err_arr_out_of_bounds: .string	"Runtime Error: Array index out of bounds!"
	.int 57
str_err_neg_arr_size: .string	"Runtime Error: Array allocation with negative array size!"
	.int 32
str_err_div_by_zero: .string	"Runtime Error: Division by zero!"
.int 5
str1: .string	"i is "
.int 0
str2: .string	""
.int 5
str3: .string	"j is "
_DV_A: 

.text

# # Check Null Ptr Reference:
# Check Null Ptr Reference:

# # static void checkNullRef(array a){
# static void checkNullRef(array a){

# # 	if(a == null) {Library.println(...);
# 	if(a == null) {Library.println(...);

# # 	Library.exit(1);
# 	Library.exit(1);

# # 	}
# 	}

# # }
# }

# __checkNullRef:
.align 4
__checkNullRef:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# 	Move a, R0
mov 8(%ebp), %eax
movl %eax, -4(%ebp)

# 	Compare 0, R0
mov -4(%ebp), %eax
cmp $0, %eax

# 	JumpTrue __checkNullRef_fault
je __checkNullRef_fault

# 	Return Rdummy
jmp __checkNullRef_epilogue

# __checkNullRef_fault:
__checkNullRef_fault:

# 	Library __println(str_err_null_ptr_ref), Rdummy
push $str_err_null_ptr_ref
call __println
add $4, %esp

# 	Library __exit(1), Rdummy
push $1
call __exit
add $4, %esp

# # End Of Method Block
# End Of Method Block
# Epilogue
__checkNullRef_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# # Check Array Index Out Of Bounds:
# Check Array Index Out Of Bounds:

# # static void checkArrayAccess(array a, index i) {
# static void checkArrayAccess(array a, index i) {

# # 	if (i<0 || i>=a.length) {
# 	if (i<0 || i>=a.length) {

# # 	Library.println("Runtime Error");
# 	Library.println("Runtime Error");

# # 	}
# 	}

# # }
# }

# __checkArrayAccess:
.align 4
__checkArrayAccess:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# 	Move i, R0
mov 12(%ebp), %eax
movl %eax, -4(%ebp)

# 	Compare 0, R0
mov -4(%ebp), %eax
cmp $0, %eax

# 	JumpL __checkArrayAccess_fault
jl __checkArrayAccess_fault

# 	ArrayLength a, R0
mov 8(%ebp), %ebx
mov -4(%ebx), %ebx
movl %ebx, -4(%ebp)

# 	Compare i, R0
mov -4(%ebp), %eax
cmp 12(%ebp), %eax

# 	JumpLE __checkArrayAccess_fault
jle __checkArrayAccess_fault

# 	Return Rdummy
jmp __checkArrayAccess_epilogue

# __checkArrayAccess_fault:
__checkArrayAccess_fault:

# 	Library __println(str_err_arr_out_of_bounds), Rdummy
push $str_err_arr_out_of_bounds
call __println
add $4, %esp

# 	Library __exit(1), Rdummy
push $1
call __exit
add $4, %esp

# # End Of Method Block
# End Of Method Block
# Epilogue
__checkArrayAccess_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# # Check Array Allocation Is Not With Negative Number:
# Check Array Allocation Is Not With Negative Number:

# # static void checkSize(size n) {
# static void checkSize(size n) {

# # 	if (n<0) Library.println("Runtime Error");
# 	if (n<0) Library.println("Runtime Error");

# # }
# }

# __checkSize:
.align 4
__checkSize:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# 	Move n, R0
mov 8(%ebp), %eax
movl %eax, -4(%ebp)

# 	Compare 0, R0
mov -4(%ebp), %eax
cmp $0, %eax

# 	JumpLE __checkSize_fault
jle __checkSize_fault

# 	Return Rdummy
jmp __checkSize_epilogue

# __checkSize_fault:
__checkSize_fault:

# 	Library __println(str_err_neg_arr_size), Rdummy
push $str_err_neg_arr_size
call __println
add $4, %esp

# 	Library __exit(1), Rdummy
push $1
call __exit
add $4, %esp

# # End Of Method Block
# End Of Method Block
# Epilogue
__checkSize_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# # Check Division By Zero:
# Check Division By Zero:

# # static void checkZero(value b) {
# static void checkZero(value b) {

# # 	if (b == 0) Library.println("Runtime Error");
# 	if (b == 0) Library.println("Runtime Error");

# # }
# }

# __checkZero:
.align 4
__checkZero:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# 	Move b, R0
mov 8(%ebp), %eax
movl %eax, -4(%ebp)

# 	Compare 0, R0
mov -4(%ebp), %eax
cmp $0, %eax

# 	JumpTrue __checkZero_fault
je __checkZero_fault

# 	Return Rdummy
jmp __checkZero_epilogue

# __checkZero_fault:
__checkZero_fault:

# 	Library __println(str_err_div_by_zero), Rdummy
push $str_err_div_by_zero
call __println
add $4, %esp

# 	Library __exit(1), Rdummy
push $1
call __exit
add $4, %esp

# # End Of Method Block
# End Of Method Block
# Epilogue
__checkZero_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# __ic_main:
.align 4
__ic_main:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $20, %esp

# Move 0, R0
movl $0, -8(%ebp)

# Move R0, i_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move 0, j_main
movl $0, -12(%ebp)

# _test_label_1:
_test_label_1:

# Move 0, R0
movl $0, -8(%ebp)

# Move 1, R1
movl $1, -16(%ebp)

# Move 2, R2
movl $2, -20(%ebp)

# Compare R1, R2
mov -20(%ebp), %eax
cmp -16(%ebp), %eax

# JumpLE _logical_op_end_3
jle _logical_op_end_3

# Move 1, R0
movl $1, -8(%ebp)

# _logical_op_end_3:
_logical_op_end_3:

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpTrue _end_label_2
je _end_label_2

# Jump _end_label_2
jmp _end_label_2

# Jump _test_label_1
jmp _test_label_1

# _end_label_2:
_end_label_2:

# _test_label_4:
_test_label_4:

# Move 0, R0
movl $0, -8(%ebp)

# Move i_main, R1
mov -4(%ebp), %eax
movl %eax, -16(%ebp)

# Move 3, R2
movl $3, -20(%ebp)

# Compare R1, R2
mov -20(%ebp), %eax
cmp -16(%ebp), %eax

# JumpLE _logical_op_end_6
jle _logical_op_end_6

# Move 1, R0
movl $1, -8(%ebp)

# _logical_op_end_6:
_logical_op_end_6:

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpTrue _end_label_5
je _end_label_5

# Move 0, R0
movl $0, -8(%ebp)

# Move R0, j_main
mov -8(%ebp), %eax
movl %eax, -12(%ebp)

# _test_label_7:
_test_label_7:

# Move 0, R0
movl $0, -8(%ebp)

# Move j_main, R1
mov -12(%ebp), %eax
movl %eax, -16(%ebp)

# Move 2, R2
movl $2, -20(%ebp)

# Compare R1, R2
mov -20(%ebp), %eax
cmp -16(%ebp), %eax

# JumpLE _logical_op_end_9
jle _logical_op_end_9

# Move 1, R0
movl $1, -8(%ebp)

# _logical_op_end_9:
_logical_op_end_9:

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpTrue _end_label_8
je _end_label_8

# Move j_main, R0
mov -12(%ebp), %eax
movl %eax, -8(%ebp)

# Move 1, R1
movl $1, -16(%ebp)

# Add R1, R0
mov -16(%ebp), %eax
add -8(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, j_main
mov -8(%ebp), %eax
movl %eax, -12(%ebp)

# Move str1, R1
movl $str1, -16(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -16(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __print(R1), Rdummy
mov -16(%ebp), %eax
push %eax
call __print
add $4, %esp

# Move i_main, R1
mov -4(%ebp), %eax
movl %eax, -16(%ebp)

# Library __printi(R1), Rdummy
mov -16(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str2, R1
movl $str2, -16(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -16(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -16(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move str3, R1
movl $str3, -16(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -16(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __print(R1), Rdummy
mov -16(%ebp), %eax
push %eax
call __print
add $4, %esp

# Move j_main, R1
mov -12(%ebp), %eax
movl %eax, -16(%ebp)

# Library __printi(R1), Rdummy
mov -16(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str2, R1
movl $str2, -16(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -16(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -16(%ebp), %eax
push %eax
call __println
add $4, %esp

# Jump _test_label_7
jmp _test_label_7

# _end_label_8:
_end_label_8:

# Move i_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Move 1, R1
movl $1, -16(%ebp)

# Add R1, R0
mov -16(%ebp), %eax
add -8(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, i_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Jump _test_label_4
jmp _test_label_4

# _end_label_5:
_end_label_5:

# Library __exit(0), Rdummy
push $0
call __exit
add $4, %esp

# # End Of Method Block
# End Of Method Block
# Epilogue
__ic_main_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

labelNPE:
push $str_err_null_ptr_ref	# error message
call __println
push $1		# error code
call __exit

labelABE:
push $str_err_arr_out_of_bounds    # error message
call __println
push $1		  # error code
call __exit

labelASE:
push $str_err_neg_arr_size  # error message
call __println
push $1       # error code
call __exit

labelDBE:
push $str_err_div_by_zero  # error message
call __println
push $1       # error code
call __exit
