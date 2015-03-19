.title "precedence_check.ic"

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
.int 0
str1: .string	""
_DV_PrecedenceCheck: 

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
sub $36, %esp

# Move 1, R0
movl $1, -8(%ebp)

# Move R0, a_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move 2, R0
movl $2, -8(%ebp)

# Move R0, b_main
mov -8(%ebp), %eax
movl %eax, -12(%ebp)

# Move 3, R0
movl $3, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move 1, R0
movl $1, -8(%ebp)

# Move R0, d_main
mov -8(%ebp), %eax
movl %eax, -20(%ebp)

# Move 0, R0
movl $0, -8(%ebp)

# Move R0, e_main
mov -8(%ebp), %eax
movl %eax, -24(%ebp)

# Move 0, f_main
movl $0, -28(%ebp)

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Move b_main, R1
mov -12(%ebp), %eax
movl %eax, -32(%ebp)

# Add R1, R0
mov -32(%ebp), %eax
add -8(%ebp), %eax
movl %eax, -8(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Add R1, R0
mov -32(%ebp), %eax
add -8(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move c_main, R0
mov -16(%ebp), %eax
movl %eax, -8(%ebp)

# Move b_main, R1
mov -12(%ebp), %eax
movl %eax, -32(%ebp)

# Sub R1, R0
mov -8(%ebp), %eax
sub -32(%ebp), %eax
movl %eax, -8(%ebp)

# Move a_main, R1
mov -4(%ebp), %eax
movl %eax, -32(%ebp)

# Sub R1, R0
mov -8(%ebp), %eax
sub -32(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Move b_main, R1
mov -12(%ebp), %eax
movl %eax, -32(%ebp)

# Mul R1, R0
mov -8(%ebp), %eax
imul -32(%ebp), %eax
movl %eax, -8(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Mul R1, R0
mov -8(%ebp), %eax
imul -32(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move c_main, R0
mov -16(%ebp), %eax
movl %eax, -8(%ebp)

# Move b_main, R1
mov -12(%ebp), %eax
movl %eax, -32(%ebp)

# StaticCall __checkZero(b=R1), Rdummy
mov -32(%ebp), %eax
cmp $0,%eax	# eax is divisor
je labelDBE     # eax == 0 ?

# Div R1, R0
mov $0, %edx
mov -8(%ebp), %eax
mov -32(%ebp), %ebx

# Move a_main, R1
mov -4(%ebp), %eax
movl %eax, -32(%ebp)

# StaticCall __checkZero(b=R1), Rdummy
mov -32(%ebp), %eax
cmp $0,%eax	# eax is divisor
je labelDBE     # eax == 0 ?

# Div R1, R0
mov $0, %edx
mov -8(%ebp), %eax
mov -32(%ebp), %ebx

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Neg R0
mov -8(%ebp), %eax
neg %eax
movl %eax, -8(%ebp)

# Neg R0
mov -8(%ebp), %eax
neg %eax
movl %eax, -8(%ebp)

# Neg R0
mov -8(%ebp), %eax
neg %eax
movl %eax, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Neg R0
mov -8(%ebp), %eax
neg %eax
movl %eax, -8(%ebp)

# Move b_main, R1
mov -12(%ebp), %eax
movl %eax, -32(%ebp)

# Mul R1, R0
mov -8(%ebp), %eax
imul -32(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move b_main, R0
mov -12(%ebp), %eax
movl %eax, -8(%ebp)

# Neg R0
mov -8(%ebp), %eax
neg %eax
movl %eax, -8(%ebp)

# Move a_main, R1
mov -4(%ebp), %eax
movl %eax, -32(%ebp)

# StaticCall __checkZero(b=R1), Rdummy
mov -32(%ebp), %eax
cmp $0,%eax	# eax is divisor
je labelDBE     # eax == 0 ?

# Div R1, R0
mov $0, %edx
mov -8(%ebp), %eax
mov -32(%ebp), %ebx

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Move a_main, R2
mov -4(%ebp), %eax
movl %eax, -36(%ebp)

# Mul R2, R1
mov -32(%ebp), %eax
imul -36(%ebp), %eax
movl %eax, -32(%ebp)

# Move b_main, R2
mov -12(%ebp), %eax
movl %eax, -36(%ebp)

# StaticCall __checkZero(b=R2), Rdummy
mov -36(%ebp), %eax
cmp $0,%eax	# eax is divisor
je labelDBE     # eax == 0 ?

# Div R2, R1
mov $0, %edx
mov -32(%ebp), %eax
mov -36(%ebp), %ebx

# Add R1, R0
mov -32(%ebp), %eax
add -8(%ebp), %eax
movl %eax, -8(%ebp)

# Move b_main, R1
mov -12(%ebp), %eax
movl %eax, -32(%ebp)

# Sub R1, R0
mov -8(%ebp), %eax
sub -32(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move c_main, R1
mov -16(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printi(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printi
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move d_main, R0
mov -20(%ebp), %eax
movl %eax, -8(%ebp)

# Xor 1, R0
mov $1,, %eax
xor -8(%ebp), %eax
movl %eax, -8(%ebp)

# Xor 1, R0
mov $1,, %eax
xor -8(%ebp), %eax
movl %eax, -8(%ebp)

# Xor 1, R0
mov $1,, %eax
xor -8(%ebp), %eax
movl %eax, -8(%ebp)

# Move R0, f_main
mov -8(%ebp), %eax
movl %eax, -28(%ebp)

# Move f_main, R1
mov -28(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printb(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printb
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move e_main, R0
mov -24(%ebp), %eax
movl %eax, -8(%ebp)

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpTrue _logical_op_end_2
je _logical_op_end_2

# Move f_main, R1
mov -28(%ebp), %eax
movl %eax, -32(%ebp)

# And R1, R0
mov -32(%ebp), %eax
and -8(%ebp), %eax
movl %eax, -8(%ebp)

# _logical_op_end_2:
_logical_op_end_2:

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpTrue _logical_op_end_1
je _logical_op_end_1

# Move d_main, R1
mov -20(%ebp), %eax
movl %eax, -32(%ebp)

# And R1, R0
mov -32(%ebp), %eax
and -8(%ebp), %eax
movl %eax, -8(%ebp)

# _logical_op_end_1:
_logical_op_end_1:

# Move R0, f_main
mov -8(%ebp), %eax
movl %eax, -28(%ebp)

# Move f_main, R1
mov -28(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printb(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printb
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move e_main, R0
mov -24(%ebp), %eax
movl %eax, -8(%ebp)

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpFalse _logical_op_end_4
jne _logical_op_end_4

# Move f_main, R1
mov -28(%ebp), %eax
movl %eax, -32(%ebp)

# Or R1, R0
mov -32(%ebp), %eax
or -8(%ebp), %eax
movl %eax, -8(%ebp)

# _logical_op_end_4:
_logical_op_end_4:

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpFalse _logical_op_end_3
jne _logical_op_end_3

# Move d_main, R1
mov -20(%ebp), %eax
movl %eax, -32(%ebp)

# Or R1, R0
mov -32(%ebp), %eax
or -8(%ebp), %eax
movl %eax, -8(%ebp)

# _logical_op_end_3:
_logical_op_end_3:

# Move R0, f_main
mov -8(%ebp), %eax
movl %eax, -28(%ebp)

# Move f_main, R1
mov -28(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printb(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printb
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move 0, R0
movl $0, -8(%ebp)

# Move e_main, R1
mov -24(%ebp), %eax
movl %eax, -32(%ebp)

# Move f_main, R2
mov -28(%ebp), %eax
movl %eax, -36(%ebp)

# Compare R1, R2
mov -36(%ebp), %eax
cmp -32(%ebp), %eax

# JumpTrue _logical_op_end_6
je _logical_op_end_6

# Move 1, R0
movl $1, -8(%ebp)

# _logical_op_end_6:
_logical_op_end_6:

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpTrue _logical_op_end_5
je _logical_op_end_5

# Move 0, R1
movl $0, -32(%ebp)

# Move a_main, R2
mov -4(%ebp), %eax
movl %eax, -36(%ebp)

# Move b_main, R3
mov -12(%ebp), %eax
movl %eax, 0(%ebp)

# Compare R2, R3
mov 0(%ebp), %eax
cmp -36(%ebp), %eax

# JumpLE _logical_op_end_7
jle _logical_op_end_7

# Move 1, R1
movl $1, -32(%ebp)

# _logical_op_end_7:
_logical_op_end_7:

# And R1, R0
mov -32(%ebp), %eax
and -8(%ebp), %eax
movl %eax, -8(%ebp)

# _logical_op_end_5:
_logical_op_end_5:

# Move R0, f_main
mov -8(%ebp), %eax
movl %eax, -28(%ebp)

# Move f_main, R1
mov -28(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printb(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printb
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

# Move f_main, R0
mov -28(%ebp), %eax
movl %eax, -8(%ebp)

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpTrue _logical_op_end_9
je _logical_op_end_9

# Move d_main, R1
mov -20(%ebp), %eax
movl %eax, -32(%ebp)

# And R1, R0
mov -32(%ebp), %eax
and -8(%ebp), %eax
movl %eax, -8(%ebp)

# _logical_op_end_9:
_logical_op_end_9:

# Compare 0, R0
mov -8(%ebp), %eax
cmp $0, %eax

# JumpFalse _logical_op_end_8
jne _logical_op_end_8

# Move e_main, R1
mov -24(%ebp), %eax
movl %eax, -32(%ebp)

# Or R1, R0
mov -32(%ebp), %eax
or -8(%ebp), %eax
movl %eax, -8(%ebp)

# _logical_op_end_8:
_logical_op_end_8:

# Move R0, f_main
mov -8(%ebp), %eax
movl %eax, -28(%ebp)

# Move f_main, R1
mov -28(%ebp), %eax
movl %eax, -32(%ebp)

# Library __printb(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __printb
add $4, %esp

# Move str1, R1
movl $str1, -32(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -32(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -32(%ebp), %eax
push %eax
call __println
add $4, %esp

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
