.title "call_check_with_inheretance.ic"

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
.int 21
str1: .string	"foo inside main class"
.int 28
str2: .string	"static foo inside main class"
.int 12
str3: .string	"foo inside X"
.int 12
str4: .string	"foo inside Y"
.int 12
str5: .string	"foo inside Z"
_DV_CallChecks:  .long _CallChecks_foo
	

_DV_X:  .long _X_foo
	

_DV_Y:  .long _Y_foo
	

_DV_Z:  .long _Z_foo
	

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
sub $32, %esp

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp

# MoveField _DV_X, R0.0
mov -8(%ebp), %ebx
movl $_DV_X, (%ebx)

# Move R0, x_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp

# MoveField _DV_Y, R0.0
mov -8(%ebp), %ebx
movl $_DV_Y, (%ebx)

# Move R0, y_main
mov -8(%ebp), %eax
movl %eax, -12(%ebp)

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp

# MoveField _DV_Z, R0.0
mov -8(%ebp), %ebx
movl $_DV_Z, (%ebx)

# Move R0, z_main
mov -8(%ebp), %eax
movl %eax, -16(%ebp)

# Move x_main, R1
mov -4(%ebp), %eax
movl %eax, -20(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -20(%ebp), %eax
cmp $0, %eax
je labelNPE

# Move 1, R2
movl $1, -24(%ebp)

# Move 2, R3
movl $2, -28(%ebp)

# VirtualCall R1.0(a=R2,b=R3), Rdummy
mov -28(%ebp), %eax
push %eax
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
add $12, %esp

# Move y_main, R1
mov -12(%ebp), %eax
movl %eax, -20(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -20(%ebp), %eax
cmp $0, %eax
je labelNPE

# Move 1, R2
movl $1, -24(%ebp)

# Move 2, R3
movl $2, -28(%ebp)

# VirtualCall R1.0(a=R2,b=R3), Rdummy
mov -28(%ebp), %eax
push %eax
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
add $12, %esp

# Move z_main, R1
mov -16(%ebp), %eax
movl %eax, -20(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -20(%ebp), %eax
cmp $0, %eax
je labelNPE

# Move 1, R2
movl $1, -24(%ebp)

# Move 2, R3
movl $2, -28(%ebp)

# VirtualCall R1.0(a=R2,b=R3), Rdummy
mov -28(%ebp), %eax
push %eax
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
add $12, %esp

# StaticCall _CallChecks_sfoo(), Rdummy
call _CallChecks_sfoo

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp

# MoveField _DV_CallChecks, R0.0
mov -8(%ebp), %ebx
movl $_DV_CallChecks, (%ebx)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -32(%ebp)

# Move c_main, R1
mov -32(%ebp), %eax
movl %eax, -20(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -20(%ebp), %eax
cmp $0, %eax
je labelNPE

# VirtualCall R1.0(), Rdummy
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)

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

# _CallChecks_foo:
.align 4
_CallChecks_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move str1, R1
movl $str1, -8(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -8(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp

# Return Rdummy
jmp _CallChecks_foo_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_CallChecks_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _CallChecks_sfoo:
.align 4
_CallChecks_sfoo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move str2, R1
movl $str2, -8(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -8(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp

# Return Rdummy
jmp _CallChecks_sfoo_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_CallChecks_sfoo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _X_foo:
.align 4
_X_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move str3, R1
movl $str3, -8(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -8(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp

# Return Rdummy
jmp _X_foo_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_X_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _Y_foo:
.align 4
_Y_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move str4, R1
movl $str4, -8(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -8(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp

# Return Rdummy
jmp _Y_foo_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_Y_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _Z_foo:
.align 4
_Z_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move str5, R1
movl $str5, -8(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -8(%ebp), %eax
cmp $0, %eax
je labelNPE

# Library __println(R1), Rdummy
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp

# Return Rdummy
jmp _Z_foo_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_Z_foo_epilogue:
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
