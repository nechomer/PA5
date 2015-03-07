.title "Quicksort.ic"

# global declarations
.global __ic_main

# data section 
.data
	.align 4

	.int 42
str_err_null_ptr_ref: .string		"Runtime Error: Null pointer dereference!"
	.int 42
str_err_arr_out_of_bounds: .string	"Runtime Error: Array index out of bounds!"
	.int 58
str_err_neg_arr_size: .string	"Runtime Error: Array allocation with negative array size!"
	.int 33
str_err_div_by_zero: .string	"Runtime Error: Division by zero!"
	.int 17
str1: .string	"Array elements: "
	.int 2
str2: .string	" "
	.int 1
str3: .string	""
	.int 25
str4: .string	"Unspecified array length"
	.int 21
str5: .string	"Invalid array length"

_DV_Quicksort:  .long _Quicksort_partition
	 .long  _Quicksort_quicksort
	 .long  _Quicksort_initArray
	 .long  _Quicksort_printArray
	

# text (code) section
	.text

