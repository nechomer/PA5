.title "array_dereference_check_err.ic"

# global declarations
.global __ic_main

# data section 
.data
	.align 4

.int 40str_err_null_ptr_ref:	.string	"Runtime Error: Null pointer dereference!"
.int 41str_err_arr_out_of_bounds: .string	"Runtime Error: Array index out of bounds!"
.int 57str_err_neg_arr_size: .string	"Runtime Error: Array allocation with negative array size!"
.int 32str_err_div_by_zero: .string	"Runtime Error: Division by zero!"
.int 4
str1: .string	"roey"
_DV_ArrDereferenceCheck: 

_DV_C:  .long _C_foo
	

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
sub $20, %esp
movl $0, -4(%ebp)
mov -4(%ebp), %eax
movl %eax, -16(%ebp)
mov -16(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $1, -20(%ebp)
mov -20(%ebp), %eax
push %eax
mov -16(%ebp), %eax
push %eax
call __checkArrayAccess
add $8, %esp
mov -16(%ebp), %eax
mov -20(%ebp), %ecx
mov (%eax, %ecx, 4), %ebx
movl %ebx, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -12(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
movl $str1, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -12(%ebp), %eax
push %eax
call __println
add $4, %esp
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
_C_foo:
# Prologue
push (%ebp)
mov %esp, (%ebp)
jmp _C_foo_epilogue
# End Of Method Block
# Epilogue
_C_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
