package com.simopr.smscompress.algorithms;

import java.util.HashMap;
import java.util.Iterator;

public class Compression {
    /**
     * Get the frequencies of each byte in the message.\n Message = 111233\n
     * returns {'1':3; '2':1; '3':2}\
     *
     * @param msg bytes of a message to count
     * @return frequencies of each byte in msg
     */
    public static HashMap<Byte, Integer> getFrequencies(byte[] msg) {
        HashMap<Byte, Integer> frequencies = new HashMap<Byte, Integer>();
        for (int i = 0; i < msg.length; i++) {
            Integer frequency = frequencies.get(msg[i]);
            if (frequency == null)
                frequencies.put(msg[i], 1);
            else
                frequencies.put(msg[i], frequency + 1);
        }
        return frequencies;
    }


    /**
     * Takes a compressed message string (compressedMsgMsg) and return the decompressed message as a Stream.
     * Compressed Message = First 1Byte (Number of Char) + Trie + Compressed Message
     *
     * @param compressedMessageStream
     * @return decompressedMsg
     */
    public static Stream decompress(Stream compressedMessageStream) {
        try {

            // get the stream of the message
            //Stream compressedMessageStream = Stream.reconstructStream(compressedMsg);

            // rebuild the trie and extract the compressed message
            Btree btree = new Btree(compressedMessageStream);

            // decompress the message
            Stream decompressedMsg = btree.expand(compressedMessageStream);

            // print "Decompressed msg result from compressed.txt (Binary):"
            return decompressedMsg;

        } catch(Exception e) {
            return null;
        }

    }

    /**
     * Compress a string and return a stream of bits.
     * (1Byte) number of Characters in the trie + Trie + Compressed message
     *
     * @param messageStream a message represented in a stream.
     * @return compressed message in a stream
     */
    public static Stream compress(Stream messageStream) {

        // if null or not a multiple of Octet, or empty stream: throw an error
        // we can only compress N*8 bits of chars with at least one character
        if (messageStream == null || messageStream.getBitPosition() != 0 || messageStream.getBytePosition() == 0) throw new NullPointerException();

        // get the frequencies of the bytes
        HashMap<Byte, Integer> frqDict = Compression.getFrequencies(messageStream.getBytesWithoutLastPaddingByte());

        // construct the btree
        Btree btree = new Btree(frqDict);

        //System.out.println(" ===== N:" + frqDict.size());
        // get the trie in binary stream
        Stream trie = btree.writeTrie();

        // getting the compressed codes
        HashMap<Byte, String> codes = btree.getCodes();


        // The total complete compressed sms composed of:
        // 1- number of characters in the trie (1 byte)
        // 2- the trie itself
        // 3- compressed sms
        // 4- padding (zeros: handle with care when decompressing)
        Stream sentSMS = new Stream();


        // add number of characters in the trie
        // It's in C2, be careful when you retrieve it
        // 0-255 (inclusive) shift with one to 1-256
        int N = codes.keySet().size();
        for (int i = 0; i < 8; i++){
            if ((N & (0b10000000 >>> i)) == 0)
                sentSMS.addBit(false);
            else
                sentSMS.addBit(true);
        }

        // add the trie to the sent SMS
        Iterator<Boolean> iteratorOfTheTrie = trie.iterator();
        while (iteratorOfTheTrie.hasNext()) {
            sentSMS.addBit(iteratorOfTheTrie.next());
        }


        // add the compressed message
        for (byte c : messageStream.getBytesWithoutLastPaddingByte()) {
            if (!codes.containsKey(c)) System.out.println("ERROR encryption");
            String sequenceOfBits = codes.get(c);
            for (char bit : sequenceOfBits.toCharArray()) {
                if (bit == '1')
                    sentSMS.addBit(true);
                else
                    sentSMS.addBit(false);
            }

        }

        // send Padding(3bits) + SizeOfTrie(8bits) + Trie(variable) + compressedMessage(variable)
        // sentSMS = trie + compressedMessage
        return sentSMS;
    }


    /**
     *
     * Check if the message has been compressed using our solution
     *
     * @param compressed message to check if it was compressed using our algorithm
     * @return true if compressed with our algorithm. false otherwise.
     */
    public static boolean checkDecompressible(Stream compressed){
        try{
            Stream message = Compression.decompress(compressed);
            if (message == null) {
                return false;
            } else {
                Stream compressedAgain = Compression.compress(message);
                //System.out.println("Compressed (Before): "+compressed.toString());
                //System.out.println("Compressed (Again ): "+compressedAgain.toString());
                //compressed
                //compressedAgain
                return compressedAgain.equals(compressed);
            }
        }catch(Exception e){
            return false;
        }
    }
    /**
     * Used for tests
     * @param args used for testing
     */
    public static void main(String[] args) {

        //List of chars
        byte[] allPossibleBytes = {(byte)0b00000000,(byte)0b00000001,(byte)0b00000010,(byte)0b00000011,(byte)0b00000100,(byte)0b00000101,(byte)0b00000110,(byte)0b00000111,(byte)0b00001000,(byte)0b00001001,(byte)0b00001010,(byte)0b00001011,(byte)0b00001100,(byte)0b00001101,(byte)0b00001110,(byte)0b00001111,(byte)0b00010000,(byte)0b00010001,(byte)0b00010010,(byte)0b00010011,(byte)0b00010100,(byte)0b00010101,(byte)0b00010110,(byte)0b00010111,(byte)0b00011000,(byte)0b00011001,(byte)0b00011010,(byte)0b00011011,(byte)0b00011100,(byte)0b00011101,(byte)0b00011110,(byte)0b00011111,(byte)0b00100000,(byte)0b00100001,(byte)0b00100010,(byte)0b00100011,(byte)0b00100100,(byte)0b00100101,(byte)0b00100110,(byte)0b00100111,(byte)0b00101000,(byte)0b00101001,(byte)0b00101010,(byte)0b00101011,(byte)0b00101100,(byte)0b00101101,(byte)0b00101110,(byte)0b00101111,(byte)0b00110000,(byte)0b00110001,(byte)0b00110010,(byte)0b00110011,(byte)0b00110100,(byte)0b00110101,(byte)0b00110110,(byte)0b00110111,(byte)0b00111000,(byte)0b00111001,(byte)0b00111010,(byte)0b00111011,(byte)0b00111100,(byte)0b00111101,(byte)0b00111110,(byte)0b00111111,(byte)0b01000000,(byte)0b01000001,(byte)0b01000010,(byte)0b01000011,(byte)0b01000100,(byte)0b01000101,(byte)0b01000110,(byte)0b01000111,(byte)0b01001000,(byte)0b01001001,(byte)0b01001010,(byte)0b01001011,(byte)0b01001100,(byte)0b01001101,(byte)0b01001110,(byte)0b01001111,(byte)0b01010000,(byte)0b01010001,(byte)0b01010010,(byte)0b01010011,(byte)0b01010100,(byte)0b01010101,(byte)0b01010110,(byte)0b01010111,(byte)0b01011000,(byte)0b01011001,(byte)0b01011010,(byte)0b01011011,(byte)0b01011100,(byte)0b01011101,(byte)0b01011110,(byte)0b01011111,(byte)0b01100000,(byte)0b01100001,(byte)0b01100010,(byte)0b01100011,(byte)0b01100100,(byte)0b01100101,(byte)0b01100110,(byte)0b01100111,(byte)0b01101000,(byte)0b01101001,(byte)0b01101010,(byte)0b01101011,(byte)0b01101100,(byte)0b01101101,(byte)0b01101110,(byte)0b01101111,(byte)0b01110000,(byte)0b01110001,(byte)0b01110010,(byte)0b01110011,(byte)0b01110100,(byte)0b01110101,(byte)0b01110110,(byte)0b01110111,(byte)0b01111000,(byte)0b01111001,(byte)0b01111010,(byte)0b01111011,(byte)0b01111100,(byte)0b01111101,(byte)0b01111110,(byte)0b01111111,(byte)0b10000000,(byte)0b10000001,(byte)0b10000010,(byte)0b10000011,(byte)0b10000100,(byte)0b10000101,(byte)0b10000110,(byte)0b10000111,(byte)0b10001000,(byte)0b10001001,(byte)0b10001010,(byte)0b10001011,(byte)0b10001100,(byte)0b10001101,(byte)0b10001110,(byte)0b10001111,(byte)0b10010000,(byte)0b10010001,(byte)0b10010010,(byte)0b10010011,(byte)0b10010100,(byte)0b10010101,(byte)0b10010110,(byte)0b10010111,(byte)0b10011000,(byte)0b10011001,(byte)0b10011010,(byte)0b10011011,(byte)0b10011100,(byte)0b10011101,(byte)0b10011110,(byte)0b10011111,(byte)0b10100000,(byte)0b10100001,(byte)0b10100010,(byte)0b10100011,(byte)0b10100100,(byte)0b10100101,(byte)0b10100110,(byte)0b10100111,(byte)0b10101000,(byte)0b10101001,(byte)0b10101010,(byte)0b10101011,(byte)0b10101100,(byte)0b10101101,(byte)0b10101110,(byte)0b10101111,(byte)0b10110000,(byte)0b10110001,(byte)0b10110010,(byte)0b10110011,(byte)0b10110100,(byte)0b10110101,(byte)0b10110110,(byte)0b10110111,(byte)0b10111000,(byte)0b10111001,(byte)0b10111010,(byte)0b10111011,(byte)0b10111100,(byte)0b10111101,(byte)0b10111110,(byte)0b10111111,(byte)0b11000000,(byte)0b11000001,(byte)0b11000010,(byte)0b11000011,(byte)0b11000100,(byte)0b11000101,(byte)0b11000110,(byte)0b11000111,(byte)0b11001000,(byte)0b11001001,(byte)0b11001010,(byte)0b11001011,(byte)0b11001100,(byte)0b11001101,(byte)0b11001110,(byte)0b11001111,(byte)0b11010000,(byte)0b11010001,(byte)0b11010010,(byte)0b11010011,(byte)0b11010100,(byte)0b11010101,(byte)0b11010110,(byte)0b11010111,(byte)0b11011000,(byte)0b11011001,(byte)0b11011010,(byte)0b11011011,(byte)0b11011100,(byte)0b11011101,(byte)0b11011110,(byte)0b11011111,(byte)0b11100000,(byte)0b11100001,(byte)0b11100010,(byte)0b11100011,(byte)0b11100100,(byte)0b11100101,(byte)0b11100110,(byte)0b11100111,(byte)0b11101000,(byte)0b11101001,(byte)0b11101010,(byte)0b11101011,(byte)0b11101100,(byte)0b11101101,(byte)0b11101110,(byte)0b11101111,(byte)0b11110000,(byte)0b11110001,(byte)0b11110010,(byte)0b11110011,(byte)0b11110100,(byte)0b11110101,(byte)0b11110110,(byte)0b11110111,(byte)0b11111000,(byte)0b11111001,(byte)0b11111010,(byte)0b11111011,(byte)0b11111100,(byte)0b11111101,(byte)0b11111110,(byte)0b11111111};



        //Checking compression and decompression of all possible chars
        String specialStringToCompress = new String(allPossibleBytes);
        Stream compressedSMSs = Compression.compress(new Stream(specialStringToCompress));
        Stream decompressedSMSs = Compression.decompress(compressedSMSs);

        Stream specialStreamToCompress = new Stream();
        for(byte b : specialStringToCompress.getBytes()){
            specialStreamToCompress.addByte(b);
        }
        //System.out.println("special:\t"+specialStreamToCompress);
        //System.out.println("decompr:\t"+decompressedSMSs);
        assert(specialStreamToCompress.equals(decompressedSMSs));


        //Checking decompression of wrong message
        for (int testCases =0; testCases < 50000; testCases++) {
            Stream stream = new Stream();
            for (int i = 0; i < 10; i++) {
                int r = (int) Math.ceil(Math.random() * allPossibleBytes.length * 2) % allPossibleBytes.length;
                stream.addByte(allPossibleBytes[r]);
            }
            if (Compression.checkDecompressible(stream)){
                System.out.println("valid random message:" + stream);
                System.out.println("       clear message:" + Compression.decompress(stream).getAsString());
            }
            //assert(!Compression.checkDecompressible(stream));
        }



        //Checking compression of message with different kind of chars
        String msgComplete = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ?,;.:/!§. }]@^|[{#&²1234567890/*-+=)çà_è-(''é&دجحخهعغفقثصضشسيصبلاتنمكطذظزوةىلارؤءئ|<>";
        System.out.println("Length of message (" + msgComplete.getBytes().length + "bytes)");

        for (int i = 1; i <= msgComplete.length(); i++) {
            System.out.println("Iteration: " + i);
            String msg = msgComplete.substring(0, i);

            Stream initialMessage = new Stream();
            for(byte b: msg.getBytes()) initialMessage.addByte(b);
            System.out.println("initialMessage ("+initialMessage.size()+"bytes) \t: " + initialMessage);
            System.out.println("                   Clear \t: " +initialMessage.getAsString());

            Stream compressedSMS = Compression.compress(new Stream(msg));
            System.out.println("compressedSMS  ("+compressedSMS.size()+"bytes) \t: " + compressedSMS);
            //System.out.println("                   Clear \t: " + new String(compressedSMS.getBytes()));

            //Check decompression
            assert(Compression.checkDecompressible(compressedSMS));

            Stream decompressedSMS = Compression.decompress(compressedSMS);
            System.out.println("decompressedSMS("+decompressedSMS.size()+"bytes) \t: "+ decompressedSMS);
            System.out.println("                   Clear \t: " + decompressedSMS.getAsString());

            assert(decompressedSMS.equals(initialMessage));
        }

    }
}
