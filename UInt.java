import java.util.Arrays;
import java.util.Scanner;

/**
 * <h1>UInt</h1>
 * Represents an unsigned integer using a boolean array to store the binary representation.
 * Each bit is stored as a boolean value, where true represents 1 and false represents 0.
 *
 * @auth Tim Fielder
 * @version 1.0 (Sept 30, 2024)
 */
public class UInt {

    // The array representing the bits of the unsigned integer.
    protected boolean[] bits;

    // The number of bits used to represent the unsigned integer.
    protected int length;

    /**
     * Constructs a new UInt by cloning an existing UInt object.
     *
     * @param toClone The UInt object to clone.
     */
    public UInt(UInt toClone) 
    {
        this.length = toClone.length;
        this.bits = Arrays.copyOf(toClone.bits, this.length);
    }

    /**
     * Constructs a new UInt from an integer value.
     * The integer is converted to its binary representation and stored in the bits array.
     *
     * @param i The integer value to convert to a UInt.
     */
    public UInt(int i) 
    {
        if (i < 0) 
        {
            throw new IllegalArgumentException("Negative values cannot be represented.");
        }

        this.length = Integer.toBinaryString(i).length();
        bits = new boolean[length];
        
        // Fill bits array with binary representation
        for (int b = length - 1; b >= 0; b--) 
        {
            bits[b] = (i % 2 == 1);
            i = i >> 1;
        }
    }

    /**
     * Creates and returns a copy of this UInt object.
     *
     * @return A new UInt object that is a clone of this instance.
     */
    @Override
    public UInt clone() 
    {
        return new UInt(this);
    }

    /**
     * Creates and returns a copy of the given UInt object.
     *
     * @param u The UInt object to clone.
     * @return A new UInt object that is a copy of the given object.
     */
    public static UInt clone(UInt u) 
    {
        return new UInt(u);
    }

    /**
     * Converts this UInt to its integer representation.
     *
     * @return The integer value corresponding to this UInt.
     */
    public int toInt()
    {
        int t = 0;
        for (int i = 0; i < length; i++) 
        {
            t = (t << 1) | (bits[i] ? 1 : 0);
        }
        return t;
    }

    /**
     * Static method to retrieve the int value from a generic UInt object.
     *
     * @param u The UInt to convert.
     * @return The int value represented by u.
     */
    public static int toInt(UInt u) 
    {
        return u.toInt();
    }

    /**
     * Returns a String representation of this binary object with a leading 0b.
     *
     * @return The constructed String.
     */
    @Override
    public String toString() 
    {
        StringBuilder s = new StringBuilder("0b0");
        for (boolean bit : bits) 
        {
            s.append(bit ? "1" : "0");
        }
        return s.toString();
    }

    private static void alignLengths(UInt a, UInt b) 
    {
        int maxLength = Math.max(a.length, b.length);
        a.extendBits(maxLength - a.length);
        b.extendBits(maxLength - b.length);
    }

    private void extendBits(int extraBits) 
    {
        boolean[] newBits = new boolean[length + extraBits];
        System.arraycopy(bits, 0, newBits, extraBits, length);
        bits = newBits;
        length += extraBits;
    }

    public void and(UInt u) 
    {
        alignLengths(this, u);
        for (int i = 0; i < this.length; i++) 
        {
            this.bits[i] = this.bits[i] & u.bits[i];
        }
    }

    public static UInt and(UInt a, UInt b) 
    {
        UInt result = a.clone();
        result.and(b);
        return result;
    }

    public void or(UInt u) 
    {
        alignLengths(this, u);
        for (int i = 0; i < this.length; i++) 
        {
            this.bits[i] = this.bits[i] | u.bits[i];
        }
    }

    public static UInt or(UInt a, UInt b) 
    {
        UInt result = a.clone();
        result.or(b);
        return result;
    }

    public void xor(UInt u) 
    {
        alignLengths(this, u);
        for (int i = 0; i < this.length; i++) 
        {
            this.bits[i] = this.bits[i] ^ u.bits[i];
        }
    }

    public static UInt xor(UInt a, UInt b) 
    {
        UInt result = a.clone();
        result.xor(b);
        return result;
    }

    public void add(UInt u) 
    {
        alignLengths(this, u);
        int carry = 0;
        for (int i = this.length - 1; i >= 0; i--) 
        {
            int sum = (this.bits[i] ? 1 : 0) + (u.bits[i] ? 1 : 0) + carry;
            this.bits[i] = (sum % 2 == 1);
            carry = sum / 2;
        }
        if (carry > 0) {
            extendBits(1);
            this.bits[0] = true;
        }
    }

    public static UInt add(UInt a, UInt b) 
    {
        UInt result = a.clone();
        result.add(b);
        return result;
    }

    public void negate() 
    {
        for (int i = 0; i < this.length; i++) 
        {
            this.bits[this.length - i - 1] = !this.bits[this.length - i - 1];
        }
        UInt one = new UInt(1);
        this.add(one);
    }

    public void sub(UInt u) 
    {
        UInt negated = u.clone();
        negated.negate();
        this.add(negated);
    }

    public static UInt sub(UInt a, UInt b) 
    {
        UInt result = a.clone();
        result.sub(b);
        return result;
    }

    public void mul(UInt u) 
    {
        int m = this.toInt();
        int q = u.toInt();
        int result = 0;

        for (int i = 0; i < Math.max(this.length, u.length); i++) 
        {
            if ((q & 1) == 1) 
            {
                result += m << i;
            }
            q >>= 1;
        }

        this.length = Integer.toBinaryString(result).length();
        bits = new boolean[length];
        for (int i = length - 1; i >= 0; i--) 
        {
            bits[i] = (result & 1) == 1;
            result >>= 1;
        }
    }

    public static UInt mul(UInt a, UInt b) 
    {
        UInt result = a.clone();
        result.mul(b);
        return result;
    }

    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the first unsigned integer: ");
        int firstValue = scanner.nextInt();
        UInt a = new UInt(firstValue);

        System.out.print("Enter the second unsigned integer: ");
        int secondValue = scanner.nextInt();
        UInt b = new UInt(secondValue);

        System.out.println("a: " + a);
        System.out.println("b: " + b);

        UInt sum = UInt.add(a, b);
        System.out.println("Sum: " + sum);

        UInt difference = UInt.sub(a, b);
        System.out.println("Difference: " + difference);

        UInt product = UInt.mul(a, b);
        System.out.println("Product: " + product);

        UInt andResult = UInt.and(a, b);
        System.out.println("AND: " + andResult);

        UInt orResult = UInt.or(a, b);
        System.out.println("OR: " + orResult);

        UInt xorResult = UInt.xor(a, b);
        System.out.println("XOR: " + xorResult);

        scanner.close();
    }
}