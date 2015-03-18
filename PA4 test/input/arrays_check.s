.title "arrays_check.ic"

# global declarations
.global __ic_main

# data section 
.data
	.align 4

str_err_null_ptr_ref:	.string	"Runtime Error: Null pointer dereference!"
str_err_arr_out_of_bounds: .string	"Runtime Error: Array index out of bounds!"
str_err_neg_arr_size: .string	"Runtime Error: Array allocation with negative array size!"
str_err_div_by_zero: .string	"Runtime Error: Division by zero!"
.int 0
str1: .string	""
_DV_Arrays: 

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
movl $5, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkSize
add $4, %esp
mov -12(%ebp), %eax
imul $4, %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %eax
movl %eax, -4(%ebp)
push $8
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %ebx
movl $_DV_Arrays, (%ebx)
mov -4(%ebp), %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $0, -16(%ebp)
mov -16(%ebp), %eax
push %eax
mov -12(%ebp), %eax
push %eax
call __checkArrayAccess
add $8, %esp
mov -12(%ebp), %eax
mov -16(%ebp), %ecx
mov -8(%ebp), %ebx
movl %ebx, (%eax, %ecx, 4)
movl $5, -8(%ebp)
mov -4(%ebp), %eax
movl %eax, -16(%ebp)
mov -16(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $0, -20(%ebp)
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
mov -12(%ebp), %ebx
mov -8(%ebp), %eax
movl %eax, 0(%ebx)
mov -4(%ebp), %eax
movl %eax, -20(%ebp)
mov -20(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $0, -24(%ebp)
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
call __checkArrayAccess
add $8, %esp
mov -20(%ebp), %eax
mov -24(%ebp), %ecx
mov (%eax, %ecx, 4), %ebx
movl %ebx, -16(%ebp)
mov -16(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -16(%ebp), %ebx
mov 0(%ebx), %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __printi
add $4, %esp
movl $str1, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -12(%ebp), %eax
push %eax
call __println
add $4, %esp
movl $0, -28(%ebp)
movl $2, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkSize
add $4, %esp
mov -12(%ebp), %eax
imul $4, %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %eax
movl %eax, -28(%ebp)
movl $39, -8(%ebp)
mov -28(%ebp), %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $1, -16(%ebp)
mov -16(%ebp), %eax
push %eax
mov -12(%ebp), %eax
push %eax
call __checkArrayAccess
add $8, %esp
mov -12(%ebp), %eax
mov -16(%ebp), %ecx
mov -8(%ebp), %ebx
movl %ebx, (%eax, %ecx, 4)
mov -28(%ebp), %eax
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
call __printi
add $4, %esp
movl $str1, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
mov -12(%ebp), %eax
push %eax
call __println
add $4, %esp
movl $3, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkSize
add $4, %esp
mov -12(%ebp), %eax
imul $4, %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -8(%ebp)
add $4, %esp
mov -8(%ebp), %eax
movl %eax, -32(%ebp)
movl $5, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkSize
add $4, %esp
mov -12(%ebp), %eax
imul $4, %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -8(%ebp)
add $4, %esp
mov -32(%ebp), %eax
movl %eax, -12(%ebp)
mov -12(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $0, -16(%ebp)
mov -16(%ebp), %eax
push %eax
mov -12(%ebp), %eax
push %eax
call __checkArrayAccess
add $8, %esp
mov -12(%ebp), %eax
mov -16(%ebp), %ecx
mov -8(%ebp), %ebx
movl %ebx, (%eax, %ecx, 4)
movl $3, -8(%ebp)
mov -32(%ebp), %eax
movl %eax, -16(%ebp)
mov -16(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $0, -20(%ebp)
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
movl $0, -16(%ebp)
mov -16(%ebp), %eax
push %eax
mov -12(%ebp), %eax
push %eax
call __checkArrayAccess
add $8, %esp
mov -12(%ebp), %eax
mov -16(%ebp), %ecx
mov -8(%ebp), %ebx
movl %ebx, (%eax, %ecx, 4)
mov -32(%ebp), %eax
movl %eax, -20(%ebp)
mov -20(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $0, -24(%ebp)
mov -24(%ebp), %eax
push %eax
mov -20(%ebp), %eax
push %eax
call __checkArrayAccess
add $8, %esp
mov -20(%ebp), %eax
mov -24(%ebp), %ecx
mov (%eax, %ecx, 4), %ebx
movl %ebx, -16(%ebp)
mov -16(%ebp), %eax
push %eax
call __checkNullRef
add $4, %esp
movl $0, -20(%ebp)
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
call __printi
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
