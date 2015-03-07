.title	"objects.ic"

# global declarations
.global __ic_main

# data section
.data
	.align 4
	
A_DV:	.long A_m
B_DV:	.long B_m

	.int 10	
str1:	.string "A fields: " 
	.int 10	
str2:	.string "B fields: " 
	.int 2	
str3:	.string ", " 
	.int 0	
str4:	.string "" 

	.int 23		
strNPC:	.string "Null pointer violation." 
	.int 23		
strABC:	.string "Array bounds violation." 

# text (code) section
.text
	
#----------------------------------------------------
	.align 4
A_m:
	push %ebp		# prologue
	mov %esp,%ebp		

	mov 8(%ebp), %eax	# itos(a)
	push 4(%eax)
	call __itos
	add $4, %esp

	push %eax		# stringCat("A fields: ", itos(a))
	push $str1
	call __stringCat
	add $8, %esp
	
	push %eax		# println
	call __println
	add $4, %esp
	
	mov %ebp,%esp		# epilogue		
	pop %ebp		
	ret			
	.align 4

	
B_m:
	push %ebp		# prologue
	mov %esp,%ebp		
	
	push $str2		# print("B fields: ")
	call __print
	add $4, %esp

	mov 8(%ebp), %eax	# printi(a)
	push 4(%eax)
	call __printi
	add $4, %esp

	push $str3		# print(", ")
	call __print
	add $4, %esp

	mov 8(%ebp), %eax	# printb(b)
	push 8(%eax)
	call __printb
	add $4, %esp

	push $str4		# println(", ")
	call __println
	add $4, %esp

	mov %ebp,%esp		# epilogue
	pop %ebp		
	ret			

#----------------------------------------------------
	.align 4
__ic_main:
	push %ebp		# prologue
	mov %esp, %ebp		
	sub $8, %esp
	push %ebx		

	movl $0, -4(%ebp)	# initialize locals
	movl $0, -8(%ebp)
	
	push $8			# oa = new A
	call __allocateObject
	add $4, %esp
	movl $A_DV, (%eax)
	mov %eax, -4(%ebp)
	
	push $12 		# ob = new B
	call __allocateObject
	add $4, %esp
	movl $B_DV, (%eax)
	mov %eax, -8(%ebp)

	mov 8(%ebp), %eax	# eax = args
	cmp $0, %eax		# null pointer check
	je labelNPC
	mov -4(%eax), %eax	# if (args.length != 0)
	cmp $0, %eax
	je L1
	
	mov -8(%ebp), %eax	# oa = ob
	mov %eax, -4(%ebp)
	
L1:	
	mov -4(%ebp), %eax	# oa.a = 412
	cmp $0, %eax		
	je labelNPC
	movl $412, 4(%eax)
	
	mov -8(%ebp), %eax	# ob.a = 413
	cmp $0, %eax		
	je labelNPC
	movl $413, 4(%eax)
	
	mov -8(%ebp), %eax	# ob.b = true
	cmp $0, %eax		
	je labelNPC
	movl $1, 8(%eax)
	
	mov -4(%ebp), %eax	# oa.m()
	cmp $0, %eax		
	je labelNPC
	push %eax
	mov (%eax), %eax
	call *(%eax)
	add $4, %esp

	pop %ebx		# epilogue
	mov %ebp,%esp		
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
