	.text
	.attribute	4, 16
	.attribute	5, "rv32i2p1_m2p0_a2p1_c2p0"
	.file	"builtin.c"
	.globl	print                           # -- Begin function print
	.p2align	1
	.type	print,@function
print:                                  # @print
# %bb.0:
	mv	a1, a0
	lui	a0, %hi(.L.str)
	addi	a0, a0, %lo(.L.str)
	tail	printf
.Lfunc_end0:
	.size	print, .Lfunc_end0-print
                                        # -- End function
	.globl	println                         # -- Begin function println
	.p2align	1
	.type	println,@function
println:                                # @println
# %bb.0:
	mv	a1, a0
	lui	a0, %hi(.L.str.1)
	addi	a0, a0, %lo(.L.str.1)
	tail	printf
.Lfunc_end1:
	.size	println, .Lfunc_end1-println
                                        # -- End function
	.globl	printInt                        # -- Begin function printInt
	.p2align	1
	.type	printInt,@function
printInt:                               # @printInt
# %bb.0:
	mv	a1, a0
	lui	a0, %hi(.L.str.2)
	addi	a0, a0, %lo(.L.str.2)
	tail	printf
.Lfunc_end2:
	.size	printInt, .Lfunc_end2-printInt
                                        # -- End function
	.globl	printlnInt                      # -- Begin function printlnInt
	.p2align	1
	.type	printlnInt,@function
printlnInt:                             # @printlnInt
# %bb.0:
	mv	a1, a0
	lui	a0, %hi(.L.str.3)
	addi	a0, a0, %lo(.L.str.3)
	tail	printf
.Lfunc_end3:
	.size	printlnInt, .Lfunc_end3-printlnInt
                                        # -- End function
	.globl	getString                       # -- Begin function getString
	.p2align	1
	.type	getString,@function
getString:                              # @getString
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	li	a0, 256
	call	malloc
	mv	a1, a0
	sw	a1, 8(sp)                       # 4-byte Folded Spill
	lui	a0, %hi(.L.str)
	addi	a0, a0, %lo(.L.str)
	call	scanf
                                        # kill: def $x11 killed $x10
	lw	a0, 8(sp)                       # 4-byte Folded Reload
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end4:
	.size	getString, .Lfunc_end4-getString
                                        # -- End function
	.globl	getInt                          # -- Begin function getInt
	.p2align	1
	.type	getInt,@function
getInt:                                 # @getInt
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	lui	a0, %hi(.L.str.2)
	addi	a0, a0, %lo(.L.str.2)
	addi	a1, sp, 8
	call	scanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end5:
	.size	getInt, .Lfunc_end5-getInt
                                        # -- End function
	.globl	toString                        # -- Begin function toString
	.p2align	1
	.type	toString,@function
toString:                               # @toString
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	sw	a0, 4(sp)                       # 4-byte Folded Spill
	li	a0, 256
	call	malloc
	lw	a2, 4(sp)                       # 4-byte Folded Reload
	sw	a0, 8(sp)                       # 4-byte Folded Spill
	lui	a1, %hi(.L.str.2)
	addi	a1, a1, %lo(.L.str.2)
	call	sprintf
                                        # kill: def $x11 killed $x10
	lw	a0, 8(sp)                       # 4-byte Folded Reload
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end6:
	.size	toString, .Lfunc_end6-toString
                                        # -- End function
	.globl	string.length                   # -- Begin function string.length
	.p2align	1
	.type	string.length,@function
string.length:                          # @string.length
# %bb.0:
	tail	strlen
.Lfunc_end7:
	.size	string.length, .Lfunc_end7-string.length
                                        # -- End function
	.globl	string.substring                # -- Begin function string.substring
	.p2align	1
	.type	string.substring,@function
string.substring:                       # @string.substring
# %bb.0:
	addi	sp, sp, -32
	sw	ra, 28(sp)                      # 4-byte Folded Spill
	sw	a2, 12(sp)                      # 4-byte Folded Spill
	sw	a1, 16(sp)                      # 4-byte Folded Spill
	sw	a0, 8(sp)                       # 4-byte Folded Spill
	li	a0, 256
	call	malloc
	lw	a2, 12(sp)                      # 4-byte Folded Reload
	lw	a1, 16(sp)                      # 4-byte Folded Reload
	sw	a0, 20(sp)                      # 4-byte Folded Spill
	mv	a0, a1
	sw	a0, 24(sp)                      # 4-byte Folded Spill
	blt	a1, a2, .LBB8_2
	j	.LBB8_1
.LBB8_1:
	lw	a0, 20(sp)                      # 4-byte Folded Reload
	lw	a1, 12(sp)                      # 4-byte Folded Reload
	lw	a2, 16(sp)                      # 4-byte Folded Reload
	sub	a1, a1, a2
	add	a2, a0, a1
	li	a1, 0
	sb	a1, 0(a2)
	lw	ra, 28(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 32
	ret
.LBB8_2:                                # =>This Inner Loop Header: Depth=1
	lw	a1, 12(sp)                      # 4-byte Folded Reload
	lw	a0, 24(sp)                      # 4-byte Folded Reload
	lw	a3, 20(sp)                      # 4-byte Folded Reload
	lw	a4, 16(sp)                      # 4-byte Folded Reload
	lw	a2, 8(sp)                       # 4-byte Folded Reload
	add	a2, a2, a0
	lbu	a2, 0(a2)
	sub	a4, a0, a4
	add	a3, a3, a4
	sb	a2, 0(a3)
	addi	a0, a0, 1
	mv	a2, a0
	sw	a2, 24(sp)                      # 4-byte Folded Spill
	beq	a0, a1, .LBB8_1
	j	.LBB8_2
.Lfunc_end8:
	.size	string.substring, .Lfunc_end8-string.substring
                                        # -- End function
	.globl	string.parseInt                 # -- Begin function string.parseInt
	.p2align	1
	.type	string.parseInt,@function
string.parseInt:                        # @string.parseInt
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	lui	a1, %hi(.L.str.2)
	addi	a1, a1, %lo(.L.str.2)
	addi	a2, sp, 8
	call	sscanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end9:
	.size	string.parseInt, .Lfunc_end9-string.parseInt
                                        # -- End function
	.globl	string.ord                      # -- Begin function string.ord
	.p2align	1
	.type	string.ord,@function
string.ord:                             # @string.ord
# %bb.0:
	add	a0, a0, a1
	lbu	a0, 0(a0)
	ret
.Lfunc_end10:
	.size	string.ord, .Lfunc_end10-string.ord
                                        # -- End function
	.globl	string.add                      # -- Begin function string.add
	.p2align	1
	.type	string.add,@function
string.add:                             # @string.add
# %bb.0:
	addi	sp, sp, -32
	sw	ra, 28(sp)                      # 4-byte Folded Spill
	sw	a1, 8(sp)                       # 4-byte Folded Spill
	sw	a0, 12(sp)                      # 4-byte Folded Spill
	li	a0, 512
	call	malloc
	mv	a1, a0
	lw	a0, 12(sp)                      # 4-byte Folded Reload
	sw	a1, 16(sp)                      # 4-byte Folded Spill
	call	strlen
	mv	a1, a0
	sw	a1, 20(sp)                      # 4-byte Folded Spill
	li	a1, 0
	sw	a1, 24(sp)                      # 4-byte Folded Spill
	bnez	a0, .LBB11_2
	j	.LBB11_1
.LBB11_1:
	lw	a0, 8(sp)                       # 4-byte Folded Reload
	call	strlen
	mv	a1, a0
	sw	a1, 0(sp)                       # 4-byte Folded Spill
	li	a1, 0
	sw	a1, 4(sp)                       # 4-byte Folded Spill
	beqz	a0, .LBB11_3
	j	.LBB11_4
.LBB11_2:                               # =>This Inner Loop Header: Depth=1
	lw	a1, 20(sp)                      # 4-byte Folded Reload
	lw	a0, 24(sp)                      # 4-byte Folded Reload
	lw	a3, 16(sp)                      # 4-byte Folded Reload
	lw	a2, 12(sp)                      # 4-byte Folded Reload
	add	a2, a2, a0
	lbu	a2, 0(a2)
	add	a3, a3, a0
	sb	a2, 0(a3)
	addi	a0, a0, 1
	mv	a2, a0
	sw	a2, 24(sp)                      # 4-byte Folded Spill
	beq	a0, a1, .LBB11_1
	j	.LBB11_2
.LBB11_3:
	lw	a0, 16(sp)                      # 4-byte Folded Reload
	lw	ra, 28(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 32
	ret
.LBB11_4:                               # =>This Inner Loop Header: Depth=1
	lw	a1, 0(sp)                       # 4-byte Folded Reload
	lw	a0, 4(sp)                       # 4-byte Folded Reload
	lw	a3, 16(sp)                      # 4-byte Folded Reload
	lw	a4, 20(sp)                      # 4-byte Folded Reload
	lw	a2, 8(sp)                       # 4-byte Folded Reload
	add	a2, a2, a0
	lbu	a2, 0(a2)
	add	a4, a4, a0
	add	a3, a3, a4
	sb	a2, 0(a3)
	addi	a0, a0, 1
	mv	a2, a0
	sw	a2, 4(sp)                       # 4-byte Folded Spill
	beq	a0, a1, .LBB11_3
	j	.LBB11_4
.Lfunc_end11:
	.size	string.add, .Lfunc_end11-string.add
                                        # -- End function
	.globl	string.eq                       # -- Begin function string.eq
	.p2align	1
	.type	string.eq,@function
string.eq:                              # @string.eq
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	call	strcmp
	seqz	a0, a0
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end12:
	.size	string.eq, .Lfunc_end12-string.eq
                                        # -- End function
	.globl	string.ne                       # -- Begin function string.ne
	.p2align	1
	.type	string.ne,@function
string.ne:                              # @string.ne
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	call	strcmp
	snez	a0, a0
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end13:
	.size	string.ne, .Lfunc_end13-string.ne
                                        # -- End function
	.globl	string.lt                       # -- Begin function string.lt
	.p2align	1
	.type	string.lt,@function
string.lt:                              # @string.lt
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	call	strcmp
	srli	a0, a0, 31
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end14:
	.size	string.lt, .Lfunc_end14-string.lt
                                        # -- End function
	.globl	string.gt                       # -- Begin function string.gt
	.p2align	1
	.type	string.gt,@function
string.gt:                              # @string.gt
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	call	strcmp
	mv	a1, a0
	li	a0, 0
	slt	a0, a0, a1
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end15:
	.size	string.gt, .Lfunc_end15-string.gt
                                        # -- End function
	.globl	string.ge                       # -- Begin function string.ge
	.p2align	1
	.type	string.ge,@function
string.ge:                              # @string.ge
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	call	strcmp
	not	a0, a0
	srli	a0, a0, 31
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end16:
	.size	string.ge, .Lfunc_end16-string.ge
                                        # -- End function
	.globl	string.le                       # -- Begin function string.le
	.p2align	1
	.type	string.le,@function
string.le:                              # @string.le
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	call	strcmp
	slti	a0, a0, 1
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end17:
	.size	string.le, .Lfunc_end17-string.le
                                        # -- End function
	.globl	array.size                      # -- Begin function array.size
	.p2align	1
	.type	array.size,@function
array.size:                             # @array.size
# %bb.0:
	lw	a0, -4(a0)
	ret
.Lfunc_end18:
	.size	array.size, .Lfunc_end18-array.size
                                        # -- End function
	.globl	_newPtrArray                    # -- Begin function _newPtrArray
	.p2align	1
	.type	_newPtrArray,@function
_newPtrArray:                           # @_newPtrArray
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	sw	a0, 8(sp)                       # 4-byte Folded Spill
	slli	a0, a0, 2
	addi	a0, a0, 4
	call	malloc
	lw	a1, 8(sp)                       # 4-byte Folded Reload
	sw	a1, 0(a0)
	addi	a0, a0, 4
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end19:
	.size	_newPtrArray, .Lfunc_end19-_newPtrArray
                                        # -- End function
	.globl	_newIntArray                    # -- Begin function _newIntArray
	.p2align	1
	.type	_newIntArray,@function
_newIntArray:                           # @_newIntArray
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	sw	a0, 8(sp)                       # 4-byte Folded Spill
	slli	a0, a0, 2
	addi	a0, a0, 4
	call	malloc
	lw	a1, 8(sp)                       # 4-byte Folded Reload
	sw	a1, 0(a0)
	addi	a0, a0, 4
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end20:
	.size	_newIntArray, .Lfunc_end20-_newIntArray
                                        # -- End function
	.globl	_newBoolArray                   # -- Begin function _newBoolArray
	.p2align	1
	.type	_newBoolArray,@function
_newBoolArray:                          # @_newBoolArray
# %bb.0:
	addi	sp, sp, -16
	sw	ra, 12(sp)                      # 4-byte Folded Spill
	sw	a0, 8(sp)                       # 4-byte Folded Spill
	addi	a0, a0, 4
	call	malloc
	lw	a1, 8(sp)                       # 4-byte Folded Reload
	sw	a1, 0(a0)
	addi	a0, a0, 4
	lw	ra, 12(sp)                      # 4-byte Folded Reload
	addi	sp, sp, 16
	ret
.Lfunc_end21:
	.size	_newBoolArray, .Lfunc_end21-_newBoolArray
                                        # -- End function
	.type	.L.str,@object                  # @.str
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"%s"
	.size	.L.str, 3

	.type	.L.str.1,@object                # @.str.1
.L.str.1:
	.asciz	"%s\n"
	.size	.L.str.1, 4

	.type	.L.str.2,@object                # @.str.2
.L.str.2:
	.asciz	"%d"
	.size	.L.str.2, 3

	.type	.L.str.3,@object                # @.str.3
.L.str.3:
	.asciz	"%d\n"
	.size	.L.str.3, 4

	.ident	"Ubuntu clang version 17.0.0 (++20230811073132+8f4dd44097c9-1~exp1~20230811073236.20)"
	.section	".note.GNU-stack","",@progbits
	.addrsig
