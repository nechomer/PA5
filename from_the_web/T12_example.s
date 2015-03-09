.title  "T12_example.ic"

# global declarations
.global __ic_main

# data section containing the dispatch vector of class A
.data
    .align 4
_DV_A: .long _A_foo

# text (code) section
.text

# class A {
#   int g;
#   void foo(int x, int y) {
#     int z = x + y;
#     g = z; // this is like this.g = z
#     Library.printi(z);
#   }
#   static void main(string[] args) {
#     A p = new A();
#     p.foo(19,2);
#   }
# }
#----------------------------------------------------
    .align 4
__ic_main:
    push %ebp               # prologue
    mov %esp,%ebp
    push $8                 # p = new A()
    call __allocateObject
    mov %eax,-4(%ebp)
    mov -4(%ebp),%ebx       # update DVPtr field
    movl $_DV_A,(%ebx)
    # p.foo(19,2) which is really like p.foo(p,19,2)
    push $2      # push y (=2)
    push $19     # push x (=19)
    push %eax    # push this (p)
    mov 0(%ebx),%ebx         # look for foo in _DV_A - foo has offset 0
    call *(%ebx)
    add $12,%esp # pop arguments
_ic_main_epilogoue:
    mov %ebp,%esp           # epilogoue
    pop %ebp
    ret

_A_foo:
    push %ebp           # prologue
    mov %esp,%ebp
    sub $12,%esp

    mov 12(%ebp),%eax   # Move x,R1
    mov %eax,-8(%ebp)
    mov 16(%ebp),%eax   # Add y,R1
    add -8(%ebp),%eax
    mov %eax,-8(%ebp)
    mov -8(%ebp),%eax   # Move R1,z
    mov %eax,-4(%ebp)
    mov 8(%ebp),%eax    # Move this,R2
    mov %eax,-12(%ebp)
    mov -8(%ebp),%eax   # MoveField R1,R2.1
    mov -12(%ebp),%ebx
    mov %eax,8(%ebx)
    mov -8(%ebp),%eax   # Library __printi(R1)
    push %eax
    call __printi
    add $4,%esp
_A_foo_epilogoue:
    mov %ebp,%esp       # epilogoue
    pop %ebp
    ret
