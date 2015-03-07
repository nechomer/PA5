.title  "arithmetic.ic"

# global declarations
.global __ic_main

# data section
.data
    .align 4
    .int 5
str1: .string "10/3="
    .int 6
str2: .string "\n10%3="

# text (code) section
.text

#----------------------------------------------------
    .align 4
__ic_main:
    push %ebp         # prologue
    mov %esp,%ebp

    push $str1
    call __print
    add $4,%esp

    # eax = edx:eax / ebx
    mov $0,%edx     # must store 0 in edx
    mov $10,%eax    # must store divisee in eax
    mov $3,%ebx
    idiv %ebx

    push %eax       # quotient is always in eax
    call __printi
    add $4,%esp

    push $str2
    call __print
    add $4,%esp

    # edx = edx:eax % ebx
    mov $0,%edx
    mov $10,%eax
    mov $3,%ebx
    idiv %ebx

    push %edx       # remainder is always in edx
    call __printi
    add $4,%esp


    mov $0,%eax    # return 0

    mov %ebp,%esp   # epilogue
    pop %ebp
    ret
