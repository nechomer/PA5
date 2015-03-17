.title "array_dereference_check_err.ic"

# global declarations
.global _ic_main

# data section 
.data
	.align 4

str_err_null_ptr_ref:	.string	"Runtime Error: Null pointer dereference!"
str_err_arr_out_of_bounds: .string	"Runtime Error: Array index out of bounds!"
str_err_neg_arr_size: .string	"Runtime Error: Array allocation with negative array size!"
str_err_div_by_zero: .string	"Runtime Error: Division by zero!"
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
push ($ebp)
mov %esp, ($ebp)
sub $4, %esp
movl $a, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jmp __checkNullRef_epilogue
.align 4
_error1:
# Prologue
push ($ebp)
mov %esp, ($ebp)
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
push ($ebp)
mov %esp, ($ebp)
sub $4, %esp
movl $i, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jl _error2
mov 8(%ebp), %ebx
mov -4(%ebx), %ebx
movl %ebx, -4(%ebp)
mov -4(%ebp), %eax
cmp 12(%ebp), %eax
jle _error2
jmp __checkArrayAccess_epilogue
.align 4
_error2:
# Prologue
push ($ebp)
mov %esp, ($ebp)
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
push ($ebp)
mov %esp, ($ebp)
sub $4, %esp
movl $n, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jle _error3
jmp __checkSize_epilogue
.align 4
_error3:
# Prologue
push ($ebp)
mov %esp, ($ebp)
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
push ($ebp)
mov %esp, ($ebp)
sub $4, %esp
movl $b, -4(%ebp)
mov -4(%ebp), %eax
cmp $0, %eax
jmp __checkZero_epilogue
.align 4
_error4:
# Prologue
push ($ebp)
mov %esp, ($ebp)
push $str_err_div_by_zero
call __println
add $4, %esp
push $1
call __exit
add $4, %esp
.align 4
_ic_main:
# Prologue
push ($ebp)
mov %esp, ($ebp)
sub $20, %esp
movl $0, -4(%ebp)
movl $arr_main, -16(%ebp)
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
push 
mov 0(%eax), %eax
call *0(%eax)
push $0
call __exit
add $4, %esp
# End Of Method Block
# Epilogue
_ic_main_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
.align 4
_C_foo:
# Prologue
push ($ebp)
mov %esp, ($ebp)
jmp _C_foo_epilogue
# End Of Method Block
# Epilogue
_C_foo_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret
