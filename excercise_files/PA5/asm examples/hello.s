.title	"hello.ic"

# global declarations
.global __ic_main

# data section
.data	
	.align 4	
	.int 13
str1: .string "Hello world\n"

# text (code) section
.text
	
#----------------------------------------------------
	.align 4
__ic_main:
	push %ebp	      # prologue
	mov %esp,%ebp	

	push $str1	      # print(...)
	call __print
	add $4, %esp	

	mov $0, %eax	# return 0
	
	mov %ebp,%esp	# epilogue
	pop %ebp	
	ret		

