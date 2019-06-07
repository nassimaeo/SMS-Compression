package com.simopr.smscompress.algorithms;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Used to create a stream of bits to represent of the binary tree and the
 * compressed message. A stream is multiple 8 (byte) elements. The last
 * byte is padded with zeros preceded by 1;
 */

public class Stream implements Iterable<Boolean> {

    class SteamIterator implements Iterator<Boolean> {

        private int bitPosIter = 0;
        private int bytePosIter = 0;

        @Override
        public boolean hasNext() {
            if(bytePosIter < bytePosition)
                return true;
            else
                return (bitPosIter < bitPosition);
        }

        @Override
        public Boolean next() {
            Byte b = stream.get(bytePosIter);
            boolean result ;
            if ((b & (0b10000000 >>> bitPosIter)) == 0b00000000)
                result = false;
            else
                result = true;
            if (++bitPosIter == SIZE){
                bytePosIter++;
                bitPosIter = 0;
            }
            return result;
        }
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new SteamIterator();
    }

    // number of bits in each byte
    private final int SIZE = 8;

    // store the bits in an array of bytes
    private ArrayList<Byte> stream;

    // position of the last Bit available
    private int bitPosition;

    // position of the last Byte available
    private int bytePosition;


    /**
     * Create a stream of bits. The last bit (1) mark
     * the end of the stream. 0 bits after this 1 bit
     * are used for padding in the last Byte of the
     * stream
     */
    public Stream() {
        // extensible array of bytes
        this.stream = new ArrayList<Byte>();

        // prepare the first byte to cater the first bits
        // initial padding: 1 (1bit) + 7 (0bits) (0b10000000)
        this.stream.add(Byte.valueOf((byte)0b10000000));
        this.bitPosition = 0;
        this.bytePosition = 0;

    }

    /**
     * Create a stream using the bytes available in the string.
     *
     * @param string build a stream from the bytes in this string
     */
    public Stream(String string) {
        // extensible array of bytes
        this.stream = new ArrayList<Byte>();

        // prepare the first byte to cater the first bits
        // initial padding: 1 (1bit) + 7 (0bits) (0b10000000)
        this.stream.add(Byte.valueOf((byte)0b10000000));
        this.bitPosition = 0;
        this.bytePosition = 0;

        //populate the stream with the bytes in the string
        for (Byte b : string.getBytes()) {
            this.addByte(b);
        }

    }


    /**
     * Add a bit 1 if bit = True, 0 otherwise
     * and set the last bit to 1 (padding limit)
     *
     * @param bit the bit to add to the stream
     */
    public void addBit(boolean bit) {
        Byte current = this.stream.get(bytePosition);
        if (bit)
            current = (byte) (current | (0b10000000 >>> this.bitPosition));
        else
            current = (byte) (current & ~(0b10000000 >>> this.bitPosition));

        this.stream.set(bytePosition, current);
        // if the buffer is full, extend with one byte to hold the next bits
        if (++this.bitPosition == SIZE) {
            this.stream.add(Byte.valueOf((byte)0b00000000));
            this.bitPosition = 0;
            this.bytePosition++;
        }

        // set the padding limit in the last byte
        current = this.stream.get(bytePosition);
        current = (byte) (current | (0b10000000 >>> this.bitPosition));
        this.stream.set(bytePosition, current);
    }

    /**
     * Put the bits of the byte code in the stream of bits
     *
     * @param code a byte
     */
    public void addByte(byte code) {
        for (int i = 0; i < SIZE; i++) {
            if ((code & (0b10000000 >>> i)) == 0)
                this.addBit(false);
            else
                this.addBit(true);
        }
    }


    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;

        if (obj instanceof Stream){
            Stream c = (Stream) obj;
            if (c.getBitPosition() != this.getBitPosition()) return false;
            if (c.getBytePosition() != this.getBytePosition()) return false;
            byte[] a = c.getBytes();
            byte[] b = this.getBytes();
            if (a.length != b.length) return false;
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) return false;
            }
            return true;
        }
        else
            return false;
    }

    public int getBytePosition() {
        return this.bytePosition;
    }

    public int getBitPosition() {
        return this.bitPosition;
    }

    /**
     * Useful for debugging
     * Print the bits in the stream as a string of 0 and 1
     * Including 3bits count of padding
     * returns a string of "1" and "0"
     * eg:
     *      "0111110101010101"
     */
    public String toString() {
        StringBuilder result = new StringBuilder("");
        int thisBytePosition = 0;
        for (Byte b : stream) {
            for (int i = 0; i < SIZE; i++) {
                if (thisBytePosition == bytePosition && this.bitPosition == i){
                    result.append('1');
                    break;
                }

                if ((b & (0b10000000 >>> i)) == 0)
                    result.append('0');
                else
                    result.append('1');
            }
            thisBytePosition++;
        }
        return result.toString();
    }


    /**
     * Translate an array of bytes of a previous stream into a stream
     * the 1bit in the last byte determines the padding start position
     * @param bytes  bytes to reconstruct
     * @return constructed stream from the bytes introduced without the last padding bits
     */
    public static Stream reconstructStream(byte[] bytes) {

        if (bytes == null || bytes.length == 0) throw new NullPointerException();

        Stream result = new Stream();

        for (int j = 0; j < bytes.length; j++) {
            byte current = bytes[j];
            int limit = 8;
            if (j == bytes.length-1)
                limit = getPaddingPosition(bytes[bytes.length-1]);

            for (int i = 0; i < limit; i++){
                if ((current & (0b10000000 >>> i)) == 0)
                    result.addBit(false);
                else
                    result.addBit(true);
            }
        }
        return result;
    }

    /**
     * Reconstruct the stream from a string representation of a previous
     * stream. Ignore the last '1' Char and all the zero after it.
     * It is used to delimit the end of the stream.
     * The string must contain only '0' and '1'.
     *
     * @param string a string of '1' and '0' characters of a stream. The last '1' is the limit
     * @return a stream reconstructed
     */
    public static Stream reconstructStream(String string) {

        if (string == null || string.length() == 0) throw new NullPointerException();

        //ignore the padding values
        int limit = string.length()-1;
        while(limit > 0 && string.charAt(limit) == '0') limit--;
        if (limit == 0 && string.charAt(limit) != '1') throw new NullPointerException();

        Stream result = new Stream();
        for (int i = 0; i < limit; i++ ) {
            if (string.charAt(i) == '0') result.addBit(false);
            else if (string.charAt(i) == '1')result.addBit(true);
            else throw new NullPointerException();
        }

        return result;
    }


    /**
     * returns the position of the most right 1bit in the byte.
     * 10000000:0; 11000000:2; 11000001:7
     *
     * @param b determine the position of the right most '1'
     * @return the position of the right most '1', 0 to 7 from left to right
     */
    private static int getPaddingPosition(byte b) {
        int position = 0;
        for (int i = 0; i < 8; i++) {
            if ( (b & (0b10000000 >>> i)) != 0) position = i;
        }
        return position;
    }




    /**
     * number of bits in the stream. Excluding the padding bits.
     *
     * @return number of bits in the stream
     */
    public int numberOfBits() {
        return this.bytePosition * SIZE + this.bitPosition;
    }

    /**
     * number of bytes in the stream, including padding bits
     *
     * @return number of bytes used to store the stream
     */
    public int size(){
        return this.bytePosition + 1;
    }


    /**
     * Get the list of all bytes stored in the stream
     * including the last byte used for padding (delimit
     * the end of the stream)
     *
     * return null if stream is empty
     *
     * @return all the bytes in the stream including padding bits
     */
    public byte[] getBytes(){

        // size of the byte in the stream
        int N = stream.size();

        byte[] result = new byte[N];
        for (int i = 0; i < N; i++) {
            result[i] = stream.get(i);
        }

        return result;
    }


    /**
     * return the list of bytes without the last byte used for padding.
     *
     * return null is the last byte isn't (0b10000000) used for padding.
     * this means bitPosition must be equal == 0
     * @return return the list of bytes without the last byte used for padding.
     */
    public byte[] getBytesWithoutLastPaddingByte(){
        // only one byte is not possible. it is either empty stream or
        // it was not built from a string
        if (this.getBytePosition() == 0) return null;

        // it was not built from a string
        if (this.getBitPosition() != 0) return null;

        // return the list of bytes without the last byte
        // size of the byte in the stream
        int N = stream.size();
        byte[] result = new byte[N-1];
        for (int i = 0; i<N-1; i++)
            result[i] = stream.get(i);
        return result;
    }



    /**
     * return the stream as a string representation without the padding byte
     * use with care because String implementation may vary from Java version
     * to another.
     * @return a string representation of the bytes.
     */
    public String getAsString(){

        // number of bytes in the stream
        int N = stream.size();

        // empty stream
        if (N == 0) return null;

        // get last 1bit position in the last byte
        int p = getPaddingPosition(stream.get(N-1));

        // It was not created from a string
        if (p != 0) return null;

        byte[] result = new byte[N-1];
        for (int i = 0; i < N - 1; i++)
            result[i] = stream.get(i);

        return new String(result);
    }



    /**
     * Used for testing
     * @param args used for testing
     */
    public static void main(String[] args) {

        //Check padding function
        System.out.println("\nCheck padding function");
        for (int i=0; i<8; i++){
            byte b = (byte) 0b10000000;
            int res = Stream.getPaddingPosition((byte)(b >>> i));
            assert(res == i);
            System.out.println("byte:" + b + " shift:" + i + " res:" + res);
        }
        int resPadding = Stream.getPaddingPosition((byte) 0b11111110);
        System.out.println("byte: 0b11111110 res:" + resPadding);




        Stream s = new Stream();
        //Check adding bits
        System.out.println("\nCheck adding bits");
        for (int i = 0; i < 10; i++){
            boolean bit = (i%2 == 0);
            System.out.print(s + " add:" + bit);
            s.addBit(bit);
            System.out.println("\t" + s + " " + s.size() + "bytes");
        }

        //Check number of bits in the stream
        System.out.println("\nCheck number of bits in the stream");
        s = new Stream();
        for (int i = 1; i < 50; i++){
            boolean bit = (i%2 == 0);
            s.addBit(bit);
            assert(s.numberOfBits() == i);
        }
        System.out.println("\t" + s + " " + s.size() + "bytes");

        //Check paddings
        System.out.println("\nCheck paddings");
        s = new Stream();
        for (int i=0; i < 10; i++){
            boolean bit = (true);
            System.out.println(bit + "\t" + s); s.addBit(bit);
        }

        //Check String Characters Reconstruction
        System.out.println("\nCheck String Characters Reconstruction");
        s = new Stream();
        String codeIt = "normalا)&àç_é' حنسيتب كشنمتسيبكن=°09}][؛×÷ طظوزءؤةر°09810927836987523/نةى كئءظؤروز ةئءظؤرزكشسيتبلعربية";
        System.out.println("Original :\t"+codeIt);
        for(byte b: codeIt.getBytes()){
            s.addByte(b);
        }
        System.out.println("As a String:\t"+s.getAsString());
        System.out.println("Binary String:\t"+s);
        System.out.println("NumberOfBits:\t" + s.numberOfBits());

        //Check cloning
        System.out.println("\nCheck cloning");
        Stream clone = Stream.reconstructStream(s.getBytes());
        System.out.println("As a String:\t"+clone.getAsString());
        System.out.println("Binary String:\t"+clone);
        System.out.println("NumberOfBits:\t" + clone.numberOfBits());

        for (int i = 1; i < codeIt.length(); i++){
            s = new Stream();
            String excerpt = codeIt.substring(0, i);
            for(byte b: excerpt.getBytes()){
                s.addByte(b);
            }
            clone = Stream.reconstructStream(s.getBytes());

            System.out.println("Binary String:\t"+clone);
            System.out.println("Binary String:\t"+s);
            assert(clone.equals(s));
            //System.out.println();
        }

        //Check GSM Encoding
        Ascii7bits gsm = new Ascii7bits();
        String wholeMessage = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ?,;.:/!§. }]@^|[{#&²1234567890/*-+=)çà_è-(''é&دجحخهعغفقثصضشسيصبلاتنمكطذظزوةىلارؤءئ|<>";;
        for (int i=1; i<=wholeMessage.length();i++){
            String messageToCompressGSM = wholeMessage.substring(0, i);
            Stream compressedMessageGSM = Compression.compress(new Stream(messageToCompressGSM));
            String gsmEncoded = gsm.stringOfBitsTo7BitsAscii(compressedMessageGSM.toString());
            String reconstrucedStringFromGSM = gsm.Ascii7BitsStringToStreamOfBits(gsmEncoded);
            Stream reconstrucedStreamFromGSM = Stream.reconstructStream(reconstrucedStringFromGSM);

            assert(Compression.checkDecompressible(reconstrucedStreamFromGSM));

            Stream decompressedMessageGSM = Compression.decompress(reconstrucedStreamFromGSM);

            //System.out.println(reconstrucedStreamFromGSM);
            //System.out.println(decompressedMessageGSM);
            System.out.println();
            System.out.println(messageToCompressGSM);
            System.out.println(decompressedMessageGSM.getAsString());
        }


        //Check reconstruction of Streams
        byte[] allPossibleBytes = {(byte)0b00000000,(byte)0b00000001,(byte)0b00000010,(byte)0b00000011,(byte)0b00000100,(byte)0b00000101,(byte)0b00000110,(byte)0b00000111,(byte)0b00001000,(byte)0b00001001,(byte)0b00001010,(byte)0b00001011,(byte)0b00001100,(byte)0b00001101,(byte)0b00001110,(byte)0b00001111,(byte)0b00010000,(byte)0b00010001,(byte)0b00010010,(byte)0b00010011,(byte)0b00010100,(byte)0b00010101,(byte)0b00010110,(byte)0b00010111,(byte)0b00011000,(byte)0b00011001,(byte)0b00011010,(byte)0b00011011,(byte)0b00011100,(byte)0b00011101,(byte)0b00011110,(byte)0b00011111,(byte)0b00100000,(byte)0b00100001,(byte)0b00100010,(byte)0b00100011,(byte)0b00100100,(byte)0b00100101,(byte)0b00100110,(byte)0b00100111,(byte)0b00101000,(byte)0b00101001,(byte)0b00101010,(byte)0b00101011,(byte)0b00101100,(byte)0b00101101,(byte)0b00101110,(byte)0b00101111,(byte)0b00110000,(byte)0b00110001,(byte)0b00110010,(byte)0b00110011,(byte)0b00110100,(byte)0b00110101,(byte)0b00110110,(byte)0b00110111,(byte)0b00111000,(byte)0b00111001,(byte)0b00111010,(byte)0b00111011,(byte)0b00111100,(byte)0b00111101,(byte)0b00111110,(byte)0b00111111,(byte)0b01000000,(byte)0b01000001,(byte)0b01000010,(byte)0b01000011,(byte)0b01000100,(byte)0b01000101,(byte)0b01000110,(byte)0b01000111,(byte)0b01001000,(byte)0b01001001,(byte)0b01001010,(byte)0b01001011,(byte)0b01001100,(byte)0b01001101,(byte)0b01001110,(byte)0b01001111,(byte)0b01010000,(byte)0b01010001,(byte)0b01010010,(byte)0b01010011,(byte)0b01010100,(byte)0b01010101,(byte)0b01010110,(byte)0b01010111,(byte)0b01011000,(byte)0b01011001,(byte)0b01011010,(byte)0b01011011,(byte)0b01011100,(byte)0b01011101,(byte)0b01011110,(byte)0b01011111,(byte)0b01100000,(byte)0b01100001,(byte)0b01100010,(byte)0b01100011,(byte)0b01100100,(byte)0b01100101,(byte)0b01100110,(byte)0b01100111,(byte)0b01101000,(byte)0b01101001,(byte)0b01101010,(byte)0b01101011,(byte)0b01101100,(byte)0b01101101,(byte)0b01101110,(byte)0b01101111,(byte)0b01110000,(byte)0b01110001,(byte)0b01110010,(byte)0b01110011,(byte)0b01110100,(byte)0b01110101,(byte)0b01110110,(byte)0b01110111,(byte)0b01111000,(byte)0b01111001,(byte)0b01111010,(byte)0b01111011,(byte)0b01111100,(byte)0b01111101,(byte)0b01111110,(byte)0b01111111,(byte)0b10000000,(byte)0b10000001,(byte)0b10000010,(byte)0b10000011,(byte)0b10000100,(byte)0b10000101,(byte)0b10000110,(byte)0b10000111,(byte)0b10001000,(byte)0b10001001,(byte)0b10001010,(byte)0b10001011,(byte)0b10001100,(byte)0b10001101,(byte)0b10001110,(byte)0b10001111,(byte)0b10010000,(byte)0b10010001,(byte)0b10010010,(byte)0b10010011,(byte)0b10010100,(byte)0b10010101,(byte)0b10010110,(byte)0b10010111,(byte)0b10011000,(byte)0b10011001,(byte)0b10011010,(byte)0b10011011,(byte)0b10011100,(byte)0b10011101,(byte)0b10011110,(byte)0b10011111,(byte)0b10100000,(byte)0b10100001,(byte)0b10100010,(byte)0b10100011,(byte)0b10100100,(byte)0b10100101,(byte)0b10100110,(byte)0b10100111,(byte)0b10101000,(byte)0b10101001,(byte)0b10101010,(byte)0b10101011,(byte)0b10101100,(byte)0b10101101,(byte)0b10101110,(byte)0b10101111,(byte)0b10110000,(byte)0b10110001,(byte)0b10110010,(byte)0b10110011,(byte)0b10110100,(byte)0b10110101,(byte)0b10110110,(byte)0b10110111,(byte)0b10111000,(byte)0b10111001,(byte)0b10111010,(byte)0b10111011,(byte)0b10111100,(byte)0b10111101,(byte)0b10111110,(byte)0b10111111,(byte)0b11000000,(byte)0b11000001,(byte)0b11000010,(byte)0b11000011,(byte)0b11000100,(byte)0b11000101,(byte)0b11000110,(byte)0b11000111,(byte)0b11001000,(byte)0b11001001,(byte)0b11001010,(byte)0b11001011,(byte)0b11001100,(byte)0b11001101,(byte)0b11001110,(byte)0b11001111,(byte)0b11010000,(byte)0b11010001,(byte)0b11010010,(byte)0b11010011,(byte)0b11010100,(byte)0b11010101,(byte)0b11010110,(byte)0b11010111,(byte)0b11011000,(byte)0b11011001,(byte)0b11011010,(byte)0b11011011,(byte)0b11011100,(byte)0b11011101,(byte)0b11011110,(byte)0b11011111,(byte)0b11100000,(byte)0b11100001,(byte)0b11100010,(byte)0b11100011,(byte)0b11100100,(byte)0b11100101,(byte)0b11100110,(byte)0b11100111,(byte)0b11101000,(byte)0b11101001,(byte)0b11101010,(byte)0b11101011,(byte)0b11101100,(byte)0b11101101,(byte)0b11101110,(byte)0b11101111,(byte)0b11110000,(byte)0b11110001,(byte)0b11110010,(byte)0b11110011,(byte)0b11110100,(byte)0b11110101,(byte)0b11110110,(byte)0b11110111,(byte)0b11111000,(byte)0b11111001,(byte)0b11111010,(byte)0b11111011,(byte)0b11111100,(byte)0b11111101,(byte)0b11111110,(byte)0b11111111};
        for (int testCases = 0; testCases < 50000; testCases++){
            Stream stream = new Stream();
            for (int i = 0; i < 10; i++){
                int r = (int) Math.ceil(Math.random() * allPossibleBytes.length * 2) % allPossibleBytes.length;
                stream.addByte(allPossibleBytes[r]);
            }
            Stream streamReconstracted = Stream.reconstructStream(stream.getBytes());
            assert(stream.equals(streamReconstracted));
            //System.out.println(stream);
            //System.out.println(streamReconstracted);
        }

        //check reconstruction of stream using strings such as this "01010101"
        for (int testCases = 1; testCases <200; testCases++){
            String onesANDzeros = "";
            for (int i = 0; i < testCases; i++){
                if (Math.random() > 0.5) onesANDzeros +="0";
                else onesANDzeros +="1";
            }
            onesANDzeros +="0"; //add the bit limit
            try {
                Stream result = Stream.reconstructStream(onesANDzeros);
                //System.out.println(result.toString()+"\n"+onesANDzeros);
                while(onesANDzeros.length() > 0 && onesANDzeros.endsWith("0"))
                    onesANDzeros = onesANDzeros.substring(0, onesANDzeros.length()-1);
                assert(result.toString().equals(onesANDzeros));
            } catch (Exception e) {
                System.out.println("wrong stream: " + onesANDzeros);
                //e.printStackTrace();
            }

        }


    }


}
