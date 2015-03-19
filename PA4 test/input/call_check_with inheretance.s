.title "call_check_with inheretance.ic"

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
# Check Null Ptr Reference:
# static void checkNullRef(array a){
# 	if(a == null) {Library.println(...);
# 	Library.exit(1);
# 	}
# }
.align 4
__checkNullRef:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp
mov 8(%ebp), %eax
movl %eax, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jmp __checkNullRef_epilogue
.align 4
__checkNullRef_epilogue:
# Prologue
push (%ebp)
mov %esp, (%ebp)
push $str_err_null_ptr_ref
call __println
add $4, %esp
push $1
call __exit
add $4, %esp
# Check Array Index Out Of Bounds:
# static void checkArrayAccess(array a, index i) {
# 	if (i<0 || i>=a.length) {
# 	Library.println("Runtime Error");
# 	}
# }
.align 4
__checkArrayAccess:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp
mov 12(%ebp), %eax
movl %eax, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jl __checkArrayAccess_epilogue
mov 8(%ebp), %ebx
mov -4(%ebx), %ebx
movl %ebx, -4(%ebp)
mov -4(%ebp), %eax
cmp 12(%ebp), %eax
jle __checkArrayAccess_epilogue
jmp __checkArrayAccess_epilogue
.align 4
__checkArrayAccess_epilogue:
# Prologue
push (%ebp)
mov %esp, (%ebp)
push $str_err_arr_out_of_bounds
call __println
add $4, %esp
push $1
call __exit
add $4, %esp
# Check Array Allocation Is Not With Negative Number:
# static void checkSize(size n) {
# 	if (n<0) Library.println("Runtime Error");
# }
.align 4
__checkSize:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp
mov 8(%ebp), %eax
movl %eax, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jle __checkSize_epilogue
jmp __checkSize_epilogue
.align 4
__checkSize_epilogue:
# Prologue
push (%ebp)
mov %esp, (%ebp)
push $str_err_neg_arr_size
call __println
add $4, %esp
push $1
call __exit
add $4, %esp
# Check Division By Zero:
# static void checkZero(value b) {
# 	if (b == 0) Library.println("Runtime Error");
# }
.align 4
__checkZero:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp
mov 8(%ebp), %eax
movl %eax, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jmp __checkZero_epilogue
.align 4
__checkZero_epilogue:
# Prologue
push (%ebp)
mov %esp, (%ebp)
push $str_err_div_by_zero
call __println
add $4, %esp
push $1
call __exit
add $4, %esp
.align 4
__ic_main:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $32, %esp
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %ebx
movl $_DV_X, (%ebx)
mov -8(%ebp), %eax
movl %eax, -4(%ebp)
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %ebx
movl $_DV_Y, (%ebx)
mov -8(%ebp), %eax
movl %eax, -12(%ebp)
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %ebx
movl $_DV_Z, (%ebx)
mov -8(%ebp), %eax
movl %eax, -16(%ebp)
mov -4(%ebp), %eax
movl %eax, -20(%ebp)
mov -20(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $1, -24(%ebp)
movl $2, -28(%ebp)
mov -28(%ebp), %eax
push %eax
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
add $12, %esp
mov -12(%ebp), %eax
movl %eax, -20(%ebp)
mov -20(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $1, -24(%ebp)
movl $2, -28(%ebp)
mov -28(%ebp), %eax
push %eax
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
add $12, %esp
mov -16(%ebp), %eax
movl %eax, -20(%ebp)
mov -20(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $1, -24(%ebp)
movl $2, -28(%ebp)
mov -28(%ebp), %eax
push %eax
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
add $12, %esp
call _CallChecks_sfoo
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %ebx
movl $_DV_CallChecks, (%ebx)
mov -8(%ebp), %eax
movl %eax, -32(%ebp)
mov -32(%ebp), %eax
movl %eax, -20(%ebp)
mov -20(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -20(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
push $0
call __exit
add $4, %esp
# End Of Method Block
# Epilogue
__ic_main_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
.align 4
_CallChecks_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp
movl $str1, -8(%ebp)
mov -8(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp
jmp _CallChecks_foo_epilogue
# End Of Method Block
# Epilogue
_CallChecks_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
.align 4
_CallChecks_sfoo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp
movl $str2, -8(%ebp)
mov -8(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp
jmp _CallChecks_sfoo_epilogue
# End Of Method Block
# Epilogue
_CallChecks_sfoo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
.align 4
_X_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp
movl $str3, -8(%ebp)
mov -8(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp
jmp _X_foo_epilogue
# End Of Method Block
# Epilogue
_X_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
.align 4
_Y_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp
movl $str4, -8(%ebp)
mov -8(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp
jmp _Y_foo_epilogue
# End Of Method Block
# Epilogue
_Y_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
.align 4
_Z_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp
movl $str5, -8(%ebp)
mov -8(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -8(%ebp), %eax
push %eax
call __println
add $4, %esp
jmp _Z_foo_epilogue
# End Of Method Block
# Epilogue
_Z_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
