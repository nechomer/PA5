.title	"fib.ic"

# global declarations
.global __ic_main
	
# data section
.data
	.align 4

	.int 0	
str1:	.string ""

	.int 23		
strNPC:	.string "Null pointer violation." 
	.int 23		
strABC:	.string "Array bounds violation." 

# text (code) section
.text	
	
#----------------------------------------------------
	.align 4
__ic_main:
	push %ebp		# prologue
	mov %esp,%ebp		
	push %ebx
	
	mov 8(%ebp), %eax	# s = args

	cmp $0, %eax		# null pointer check
	je labelNPC

				# array bounds check
	mov -4(%eax), %ebx	# ebx = length
	mov $0, %ecx		# ecx = index
	cmp %ecx, %ebx
	jle labelABC		# ebx <= ecx ?
	cmp $0, %ecx
	jl  labelABC		# ecx < 0 ?

	mov (%eax,%ecx,4), %edx # s = args[0]

	push $-1		# n = stoi(s,-1)
	push %edx
	call __stoi
	add $8, %esp

	push %eax		# r = fib(n)
	call Fibonacci_fib
	add $4, %esp		
	
	push %eax		# printi(r)
	call __printi	
	add $4, %esp		

	push $str1		# println("")
	call __println	
	add $4, %esp
	
	pop %ebx		# epilogue		
	mov %ebp,%esp		
	pop %ebp		
	ret			
	
	
#----------------------------------------------------
	.align 4
Fibonacci_fib:
	push %ebp		# prologue
	mov %esp, %ebp		
	push %ebx		

	cmp $2, 8(%ebp)		# if (n < 2)
	jge L1
	
	mov 8(%ebp), %eax	# return n
	jmp epilogue_Fibonacci_fib	
	
L1:						
	mov 8(%ebp), %eax	# t1 = fib(n-1)
	dec %eax
	push %eax
	call Fibonacci_fib
	add $4, %esp

	mov %eax, %ebx		# ebx = t1

	mov 8(%ebp), %eax	# t2 = fib(n-2)
	sub $2, %eax
	push %eax
	call Fibonacci_fib
	add $4, %esp

	add %ebx, %eax		# return t1+t2 

epilogue_Fibonacci_fib:
	pop %ebx		
	mov %ebp, %esp		
	pop %ebp		
	ret			

#----------------------------------------------------

	.align 4
labelNPC:	
	push $strNPC
	call __println
	push $1
	call __exit

	.align 4
labelABC:		
	push $strABC
	call __println
	push $1
	call __exit

