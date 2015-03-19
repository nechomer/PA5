.title "return_check.ic"

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
_DV_ReturnCheck:  .long _ReturnCheck_checkRetFuncs
	 .long  _ReturnCheck_retvoid
	 .long  _ReturnCheck_retvobj
	 .long  _ReturnCheck_retArr
	 .long  _ReturnCheck_retArrLocation
	 .long  _ReturnCheck_retThis
	 .long  _ReturnCheck_retNewObjThis
	 .long  _ReturnCheck_retNewObj
	 .long  _ReturnCheck_retNewArr
	 .long  _ReturnCheck_retArrLength
	 .long  _ReturnCheck_retNegation
	 .long  _ReturnCheck_retNegative
	 .long  _ReturnCheck_retNull
	 .long  _ReturnCheck_retIntCalling
	

_DV_C:  .long _C_retThis
	

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
sub $8, %esp

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp

# MoveField _DV_ReturnCheck, R0.0
mov -8(%ebp), %ebx
movl $_DV_ReturnCheck, (%ebx)

# Move R0, rc_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move rc_main, R1
mov -4(%ebp), %eax
movl %eax, 0(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov 0(%ebp), %eax
cmp $0, %eax
je labelNPE

# VirtualCall R1.0(), Rdummy
mov 0(%ebp), %eax
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

# _ReturnCheck_checkRetFuncs:
.align 4
_ReturnCheck_checkRetFuncs:
# Prologue
push (%ebp)
mov %esp, (%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.1(), Rdummy
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *4(%eax)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.2(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *8(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.3(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *12(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.4(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *16(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.5(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *20(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.6(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *24(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.7(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *28(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.8(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *32(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.9(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *36(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.10(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *40(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.11(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *44(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.12(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *48(%eax)
movl %eax, 0(%ebp)

# Move this, R1
mov 0(%ebp), %eax
movl %eax, 0(%ebp)

# VirtualCall R1.13(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *52(%eax)
movl %eax, 0(%ebp)

# Return Rdummy
jmp _ReturnCheck_checkRetFuncs_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_checkRetFuncs_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retvoid:
.align 4
_ReturnCheck_retvoid:
# Prologue
push (%ebp)
mov %esp, (%ebp)

# Return Rdummy
jmp _ReturnCheck_retvoid_epilogue

# Return Rdummy
jmp _ReturnCheck_retvoid_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retvoid_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retvobj:
.align 4
_ReturnCheck_retvobj:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp

# MoveField _DV_C, R0.0
mov -8(%ebp), %ebx
movl $_DV_C, (%ebx)

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move c_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Return R0
mov -8(%ebp), %eax
jmp _ReturnCheck_retvobj_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retvobj_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retArr:
.align 4
_ReturnCheck_retArr:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $12, %esp

# Move 2, R1
movl $2, -12(%ebp)

# StaticCall __checkSize(n=R1), Rdummy
mov -12(%ebp), %eax
cmp $0,%eax		# eax == array size
jle labelASE    # eax <= 0 ?

# Mul 4, R1
mov -12(%ebp), %eax
imul $4, %eax
movl %eax, -12(%ebp)

# Library __allocateArray(R1), R0
mov -12(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -8(%ebp)
add $4, %esp

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move c_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Return R0
mov -8(%ebp), %eax
jmp _ReturnCheck_retArr_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retArr_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retArrLocation:
.align 4
_ReturnCheck_retArrLocation:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $16, %esp

# Move 0, R1
movl $0, -12(%ebp)

# StaticCall __checkSize(n=R1), Rdummy
mov -12(%ebp), %eax
cmp $0,%eax		# eax == array size
jle labelASE    # eax <= 0 ?

# Mul 4, R1
mov -12(%ebp), %eax
imul $4, %eax
movl %eax, -12(%ebp)

# Library __allocateArray(R1), R0
mov -12(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -8(%ebp)
add $4, %esp

# Move R0, c_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -8(%ebp)
add $4, %esp

# MoveField _DV_C, R0.0
mov -8(%ebp), %ebx
movl $_DV_C, (%ebx)

# Move c_main, R1
mov -4(%ebp), %eax
movl %eax, -12(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -12(%ebp), %eax
cmp $0, %eax
je labelNPE

# Move 0, R2
movl $0, -16(%ebp)

# StaticCall __checkArrayAccess(a=R1,i=R2), Rdummy
mov -16(%ebp), %ecx
mov -12(%ebp), %eax
mov -4(%eax),%edx  # edx = length
cmp %ecx,%edx
jle labelABE       # edx <= ecx ?
cmp $0,%ecx
jl  labelABE       # ecx < 0 ?

# MoveArray R0, R1[R2]
mov -12(%ebp), %eax
mov -16(%ebp), %ecx
mov -8(%ebp), %ebx
movl %ebx, (%eax, %ecx, 4)

# Move c_main, R1
mov -4(%ebp), %eax
movl %eax, -12(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -12(%ebp), %eax
cmp $0, %eax
je labelNPE

# Move 1, R2
movl $1, -16(%ebp)

# StaticCall __checkArrayAccess(a=R1,i=R2), Rdummy
mov -16(%ebp), %ecx
mov -12(%ebp), %eax
mov -4(%eax),%edx  # edx = length
cmp %ecx,%edx
jle labelABE       # edx <= ecx ?
cmp $0,%ecx
jl  labelABE       # ecx < 0 ?

# MoveArray R1[R2], R0
mov -12(%ebp), %eax
mov -16(%ebp), %ecx
mov (%eax, %ecx, 4), %ebx
movl %ebx, -8(%ebp)

# Return R0
mov -8(%ebp), %eax
jmp _ReturnCheck_retArrLocation_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retArrLocation_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retThis:
.align 4
_ReturnCheck_retThis:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# Library __allocateObject(4), R1
push $4
call __allocateObject
movl %eax, 0(%ebp)
add $4, %esp

# MoveField _DV_C, R1.0
mov 0(%ebp), %ebx
movl $_DV_C, (%ebx)

# StaticCall __checkNullRef(a=R1), Rdummy
mov 0(%ebp), %eax
cmp $0, %eax
je labelNPE

# VirtualCall R1.0(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
movl %eax, -4(%ebp)

# Return R0
mov -4(%ebp), %eax
jmp _ReturnCheck_retThis_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retThis_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retNewObjThis:
.align 4
_ReturnCheck_retNewObjThis:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# Library __allocateObject(4), R1
push $4
call __allocateObject
movl %eax, 0(%ebp)
add $4, %esp

# MoveField _DV_C, R1.0
mov 0(%ebp), %ebx
movl $_DV_C, (%ebx)

# StaticCall __checkNullRef(a=R1), Rdummy
mov 0(%ebp), %eax
cmp $0, %eax
je labelNPE

# VirtualCall R1.0(), R0
mov 0(%ebp), %eax
push %eax
mov 0(%eax), %eax
call *0(%eax)
movl %eax, -4(%ebp)

# Return R0
mov -4(%ebp), %eax
jmp _ReturnCheck_retNewObjThis_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retNewObjThis_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retNewObj:
.align 4
_ReturnCheck_retNewObj:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# Library __allocateObject(4), R0
push $4
call __allocateObject
movl %eax, -4(%ebp)
add $4, %esp

# MoveField _DV_C, R0.0
mov -4(%ebp), %ebx
movl $_DV_C, (%ebx)

# Return R0
mov -4(%ebp), %eax
jmp _ReturnCheck_retNewObj_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retNewObj_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retNewArr:
.align 4
_ReturnCheck_retNewArr:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move 34, R1
movl $34, -8(%ebp)

# StaticCall __checkSize(n=R1), Rdummy
mov -8(%ebp), %eax
cmp $0,%eax		# eax == array size
jle labelASE    # eax <= 0 ?

# Mul 4, R1
mov -8(%ebp), %eax
imul $4, %eax
movl %eax, -8(%ebp)

# Library __allocateArray(R1), R0
mov -8(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -4(%ebp)
add $4, %esp

# Return R0
mov -4(%ebp), %eax
jmp _ReturnCheck_retNewArr_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retNewArr_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retArrLength:
.align 4
_ReturnCheck_retArrLength:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $12, %esp

# Move 2, R1
movl $2, -12(%ebp)

# StaticCall __checkSize(n=R1), Rdummy
mov -12(%ebp), %eax
cmp $0,%eax		# eax == array size
jle labelASE    # eax <= 0 ?

# Mul 4, R1
mov -12(%ebp), %eax
imul $4, %eax
movl %eax, -12(%ebp)

# Library __allocateArray(R1), R0
mov -12(%ebp), %eax
push %eax
call __allocateArray
movl %eax, -8(%ebp)
add $4, %esp

# Move R0, arr_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move arr_main, R1
mov -4(%ebp), %eax
movl %eax, -12(%ebp)

# StaticCall __checkNullRef(a=R1), Rdummy
mov -12(%ebp), %eax
cmp $0, %eax
je labelNPE

# ArrayLength R1, R0
mov -12(%ebp), %ebx
mov -4(%ebx), %ebx
movl %ebx, -8(%ebp)

# Return R0
mov -8(%ebp), %eax
jmp _ReturnCheck_retArrLength_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retArrLength_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retNegation:
.align 4
_ReturnCheck_retNegation:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move 1, R0
movl $1, -8(%ebp)

# Move R0, a_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Xor 1, R0
mov $1,, %eax
xor -8(%ebp), %eax
movl %eax, -8(%ebp)

# Return R0
mov -8(%ebp), %eax
jmp _ReturnCheck_retNegation_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retNegation_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retNegative:
.align 4
_ReturnCheck_retNegative:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $8, %esp

# Move 1, R0
movl $1, -8(%ebp)

# Move R0, a_main
mov -8(%ebp), %eax
movl %eax, -4(%ebp)

# Move a_main, R0
mov -4(%ebp), %eax
movl %eax, -8(%ebp)

# Neg R0
mov -8(%ebp), %eax
neg %eax
movl %eax, -8(%ebp)

# Return R0
mov -8(%ebp), %eax
jmp _ReturnCheck_retNegative_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retNegative_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retNull:
.align 4
_ReturnCheck_retNull:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# Move 0, R0
movl $0, -4(%ebp)

# Return R0
mov -4(%ebp), %eax
jmp _ReturnCheck_retNull_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retNull_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _ReturnCheck_retIntCalling:
.align 4
_ReturnCheck_retIntCalling:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# Move 6, R0
movl $6, -4(%ebp)

# Return R0
mov -4(%ebp), %eax
jmp _ReturnCheck_retIntCalling_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_ReturnCheck_retIntCalling_epilogue:
mov (%ebp), %esp
pop (%ebp)
ret

# _C_retThis:
.align 4
_C_retThis:
# Prologue
push (%ebp)
mov %esp, (%ebp)
sub $4, %esp

# Move this, R0
mov 0(%ebp), %eax
movl %eax, -4(%ebp)

# Return R0
mov -4(%ebp), %eax
jmp _C_retThis_epilogue

# # End Of Method Block
# End Of Method Block
# Epilogue
_C_retThis_epilogue:
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
