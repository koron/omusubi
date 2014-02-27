package main

import (
	"fmt"
)

func getMask(bits int) uint32 {
	return 0xffffffff >> (uint)(32 - bits)
}

func genUnpack(bits int) {
	bufName := "buf"
	fmt.Printf(`
    static void unpack%d(
            int[] %s,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x%x;
        int n, c;
`, bits, bufName, getMask(bits))
	fmt.Println("")

	genUnpackBody(bits, bufName);

	fmt.Printf(`
        dst.write(%s, 0, %d);
    }
`, bufName, 32)
}

func genUnpackBody(bits int, bufName string) {
	idx := 0
	remain := 0
	for i := 0; i < bits; i += 1 {
		fmt.Println("        n = src.get();")
		if remain > 0 {
			fmt.Printf("        %s[%2d] = filter.filterInt(c | n >>> %2d);\n", bufName, idx, remain + 32 - bits)
			idx += 1
			remain -= bits
		}
		remain += 32
		for remain >= bits {
			remain -= bits
			fmt.Printf("        %s[%2d] = filter.filterInt(n >>> %2d & m);\n", bufName, idx, remain)
			idx += 1
		}
		if remain > 0 {
			fmt.Printf("        c = n << %2d & m;\n", bits - remain)
		}
	}
}

func main() {
	fmt.Printf(`package net.kaoriya.omusubi;

import java.nio.IntBuffer;

// This class was generated automatically, don't modify.
class IntBitPackingUnpacks
{
`)
	for i := 1; i <= 32; i += 1 {
		genUnpack(i)
	}
	fmt.Println("\n}")
}
