package main

import (
	"fmt"
)

func getMask(bits int) uint32 {
	return 0xffffffff >> (uint)(32 - bits)
}

func genPack(bits int) {
	bufName := "buf"
	fmt.Printf(`
    static void pack%d(
			int[] %s,
            IntBuffer src,
            IntOutputStream dst,
            IntFilter filter)
    {
        final int m = 0x%x;
`, bits, bufName, getMask(bits))
	if 32 % bits != 0 {
		fmt.Println("        int n;")
	}
	fmt.Println("")

	genPackBody(bits, bufName);

	fmt.Printf(`
        dst.write(%s, 0, %d);
    }
`, bufName, bits)
}

func genPackBody(bits int, bufName string) {
	remain := 0
	for i := 0; i < bits; i += 1 {
		capacity := 32;
		fmt.Printf("        %s[%2d] =\n", bufName, i)
		if remain > 0 {
			fmt.Printf("            (n << %2d) |\n", capacity - remain)
			capacity -= remain
			remain = 0
		}
		for capacity > 0 {
			if capacity > bits {
				fmt.Printf("            (filter.filterInt(src.get()) & m) << %2d |\n", capacity - bits)
				capacity -= bits
			} else if capacity == bits {
				fmt.Printf("            (filter.filterInt(src.get()) & m);\n")
				remain = 0
				capacity = 0
			} else {
				fmt.Printf("            (n = filter.filterInt(src.get()) & m) >>> %2d;\n", bits - capacity)
				remain = bits - capacity
				capacity = 0
			}
		}
	}
}

func main() {
	fmt.Printf(`package net.kaoriya.omusubi;

import java.nio.IntBuffer;

class IntBitPackingPacks
{
`)
	for i := 1; i <= 32; i += 1 {
		genPack(i)
	}
	fmt.Println("\n}")
}
