package com.simopr.smscompress.algorithms;

import java.util.HashMap;

public class Ascii7bits {

    private HashMap<String, Character> gsmCodes;
    private HashMap<Character, String> gsmCodesReversed;

    //https://en.wikipedia.org/wiki/GSM_03.38
    //Bug Solution: Sony phones treat \r as \n so remove \r and replace it with: { (14bits)
    //Bug Solution: Euro character(14bits) is used to represent ESC(7bits) character. Because
    //only {}[]^€|~\ extension chars of ASCII 7bits are allowed after ESC if it is not one of these
    //Android will not send the SMS an create a failed result

    public Ascii7bits() {
        gsmCodesReversed = new HashMap<Character, String>();
        gsmCodesReversed.put('Δ',"0000001");
        gsmCodesReversed.put(' ',"0000010");
        gsmCodesReversed.put('0',"0000011");
        gsmCodesReversed.put('¡',"0000100");
        gsmCodesReversed.put('P',"0000101");
        gsmCodesReversed.put('¿',"0000110");
        gsmCodesReversed.put('p',"0000111");
        gsmCodesReversed.put('£',"0001000");
        gsmCodesReversed.put('_',"0001001");
        gsmCodesReversed.put('!',"0001010");
        gsmCodesReversed.put('1',"0001011");
        gsmCodesReversed.put('A',"0001100");
        gsmCodesReversed.put('Q',"0001101");
        gsmCodesReversed.put('a',"0001110");
        gsmCodesReversed.put('q',"0001111");
        gsmCodesReversed.put('$',"0010000");
        gsmCodesReversed.put('Φ',"0010001");
        gsmCodesReversed.put('"',"0010010");
        gsmCodesReversed.put('2',"0010011");
        gsmCodesReversed.put('B',"0010100");
        gsmCodesReversed.put('R',"0010101");
        gsmCodesReversed.put('b',"0010110");
        gsmCodesReversed.put('r',"0010111");
        gsmCodesReversed.put('¥',"0011000");
        gsmCodesReversed.put('Γ',"0011001");
        gsmCodesReversed.put('#',"0011010");
        gsmCodesReversed.put('3',"0011011");
        gsmCodesReversed.put('C',"0011100");
        gsmCodesReversed.put('S',"0011101");
        gsmCodesReversed.put('c',"0011110");
        gsmCodesReversed.put('s',"0011111");
        gsmCodesReversed.put('è',"0100000");
        gsmCodesReversed.put('Λ',"0100001");
        gsmCodesReversed.put('¤',"0100010");
        gsmCodesReversed.put('4',"0100011");
        gsmCodesReversed.put('D',"0100100");
        gsmCodesReversed.put('T',"0100101");
        gsmCodesReversed.put('d',"0100110");
        gsmCodesReversed.put('t',"0100111");
        gsmCodesReversed.put('é',"0101000");
        gsmCodesReversed.put('Ω',"0101001");
        gsmCodesReversed.put('%',"0101010");
        gsmCodesReversed.put('5',"0101011");
        gsmCodesReversed.put('E',"0101100");
        gsmCodesReversed.put('U',"0101101");
        gsmCodesReversed.put('e',"0101110");
        gsmCodesReversed.put('u',"0101111");
        gsmCodesReversed.put('ù',"0110000");
        gsmCodesReversed.put('Π',"0110001");
        gsmCodesReversed.put('&',"0110010");
        gsmCodesReversed.put('6',"0110011");
        gsmCodesReversed.put('F',"0110100");
        gsmCodesReversed.put('V',"0110101");
        gsmCodesReversed.put('f',"0110110");
        gsmCodesReversed.put('v',"0110111");
        gsmCodesReversed.put('ì',"0111000");
        gsmCodesReversed.put('Ψ',"0111001");
        gsmCodesReversed.put('\'',"0111010");
        gsmCodesReversed.put('7',"0111011");
        gsmCodesReversed.put('G',"0111100");
        gsmCodesReversed.put('W',"0111101");
        gsmCodesReversed.put('g',"0111110");
        gsmCodesReversed.put('w',"0111111");
        gsmCodesReversed.put('ò',"1000000");
        gsmCodesReversed.put('Σ',"1000001");
        gsmCodesReversed.put('(',"1000010");
        gsmCodesReversed.put('8',"1000011");
        gsmCodesReversed.put('H',"1000100");
        gsmCodesReversed.put('X',"1000101");
        gsmCodesReversed.put('h',"1000110");
        gsmCodesReversed.put('x',"1000111");
        gsmCodesReversed.put('Ç',"1001000");
        gsmCodesReversed.put('Θ',"1001001");
        gsmCodesReversed.put(')',"1001010");
        gsmCodesReversed.put('9',"1001011");
        gsmCodesReversed.put('I',"1001100");
        gsmCodesReversed.put('Y',"1001101");
        gsmCodesReversed.put('i',"1001110");
        gsmCodesReversed.put('y',"1001111");
        gsmCodesReversed.put('Ξ',"1010000");
        gsmCodesReversed.put('*',"1010001");
        gsmCodesReversed.put(':',"1010010");
        gsmCodesReversed.put('J',"1010011");
        gsmCodesReversed.put('Z',"1010100");
        gsmCodesReversed.put('j',"1010101");
        gsmCodesReversed.put('z',"1010110");
        gsmCodesReversed.put('Ø',"1010111");
        gsmCodesReversed.put('+',"1011000");
        gsmCodesReversed.put(';',"1011001");
        gsmCodesReversed.put('K',"1011010");
        gsmCodesReversed.put('Ä',"1011011");
        gsmCodesReversed.put('k',"1011100");
        gsmCodesReversed.put('ä',"1011101");
        gsmCodesReversed.put('ø',"1011110");
        gsmCodesReversed.put('Æ',"1011111");
        gsmCodesReversed.put(',',"1100000");
        gsmCodesReversed.put('<',"1100001");
        gsmCodesReversed.put('L',"1100010");
        gsmCodesReversed.put('Ö',"1100011");
        gsmCodesReversed.put('l',"1100100");
        gsmCodesReversed.put('ö',"1100101");
        gsmCodesReversed.put('æ',"1100110");
        gsmCodesReversed.put('-',"1100111");
        gsmCodesReversed.put('=',"1101000");
        gsmCodesReversed.put('M',"1101001");
        gsmCodesReversed.put('Ñ',"1101010");
        gsmCodesReversed.put('m',"1101011");
        gsmCodesReversed.put('ñ',"1101100");
        gsmCodesReversed.put('Å',"1101101");
        gsmCodesReversed.put('ß',"1101110");
        gsmCodesReversed.put('.',"1101111");
        gsmCodesReversed.put('>',"1110000");
        gsmCodesReversed.put('N',"1110001");
        gsmCodesReversed.put('Ü',"1110010");
        gsmCodesReversed.put('n',"1110011");
        gsmCodesReversed.put('ü',"1110100");
        gsmCodesReversed.put('å',"1110101");
        gsmCodesReversed.put('É',"1110110");
        gsmCodesReversed.put('/',"1110111");
        gsmCodesReversed.put('?',"1111000");
        gsmCodesReversed.put('O',"1111001");
        gsmCodesReversed.put('§',"1111010");
        gsmCodesReversed.put('o',"1111011");
        gsmCodesReversed.put('à',"1111100");
        gsmCodesReversed.put('@',"1111101");
        gsmCodesReversed.put('\n',"1111110");//LF\n
        gsmCodesReversed.put('€',"1111111");//ESC char c=0b0011011;
        gsmCodesReversed.put('{',"0000000");//CR\r


        gsmCodes = new HashMap<String, Character>();
        gsmCodes.put("0000001",'Δ');
        gsmCodes.put("0000010",' ');
        gsmCodes.put("0000011",'0');
        gsmCodes.put("0000100",'¡');
        gsmCodes.put("0000101",'P');
        gsmCodes.put("0000110",'¿');
        gsmCodes.put("0000111",'p');
        gsmCodes.put("0001000",'£');
        gsmCodes.put("0001001",'_');
        gsmCodes.put("0001010",'!');
        gsmCodes.put("0001011",'1');
        gsmCodes.put("0001100",'A');
        gsmCodes.put("0001101",'Q');
        gsmCodes.put("0001110",'a');
        gsmCodes.put("0001111",'q');
        gsmCodes.put("0010000",'$');
        gsmCodes.put("0010001",'Φ');
        gsmCodes.put("0010010",'"');
        gsmCodes.put("0010011",'2');
        gsmCodes.put("0010100",'B');
        gsmCodes.put("0010101",'R');
        gsmCodes.put("0010110",'b');
        gsmCodes.put("0010111",'r');
        gsmCodes.put("0011000",'¥');
        gsmCodes.put("0011001",'Γ');
        gsmCodes.put("0011010",'#');
        gsmCodes.put("0011011",'3');
        gsmCodes.put("0011100",'C');
        gsmCodes.put("0011101",'S');
        gsmCodes.put("0011110",'c');
        gsmCodes.put("0011111",'s');
        gsmCodes.put("0100000",'è');
        gsmCodes.put("0100001",'Λ');
        gsmCodes.put("0100010",'¤');
        gsmCodes.put("0100011",'4');
        gsmCodes.put("0100100",'D');
        gsmCodes.put("0100101",'T');
        gsmCodes.put("0100110",'d');
        gsmCodes.put("0100111",'t');
        gsmCodes.put("0101000",'é');
        gsmCodes.put("0101001",'Ω');
        gsmCodes.put("0101010",'%');
        gsmCodes.put("0101011",'5');
        gsmCodes.put("0101100",'E');
        gsmCodes.put("0101101",'U');
        gsmCodes.put("0101110",'e');
        gsmCodes.put("0101111",'u');
        gsmCodes.put("0110000",'ù');
        gsmCodes.put("0110001",'Π');
        gsmCodes.put("0110010",'&');
        gsmCodes.put("0110011",'6');
        gsmCodes.put("0110100",'F');
        gsmCodes.put("0110101",'V');
        gsmCodes.put("0110110",'f');
        gsmCodes.put("0110111",'v');
        gsmCodes.put("0111000",'ì');
        gsmCodes.put("0111001",'Ψ');
        gsmCodes.put("0111010",'\'');
        gsmCodes.put("0111011",'7');
        gsmCodes.put("0111100",'G');
        gsmCodes.put("0111101",'W');
        gsmCodes.put("0111110",'g');
        gsmCodes.put("0111111",'w');
        gsmCodes.put("1000000",'ò');
        gsmCodes.put("1000001",'Σ');
        gsmCodes.put("1000010",'(');
        gsmCodes.put("1000011",'8');
        gsmCodes.put("1000100",'H');
        gsmCodes.put("1000101",'X');
        gsmCodes.put("1000110",'h');
        gsmCodes.put("1000111",'x');
        gsmCodes.put("1001000",'Ç');
        gsmCodes.put("1001001",'Θ');
        gsmCodes.put("1001010",')');
        gsmCodes.put("1001011",'9');
        gsmCodes.put("1001100",'I');
        gsmCodes.put("1001101",'Y');
        gsmCodes.put("1001110",'i');
        gsmCodes.put("1001111",'y');
        gsmCodes.put("1010000",'Ξ');
        gsmCodes.put("1010001",'*');
        gsmCodes.put("1010010",':');
        gsmCodes.put("1010011",'J');
        gsmCodes.put("1010100",'Z');
        gsmCodes.put("1010101",'j');
        gsmCodes.put("1010110",'z');
        gsmCodes.put("1010111",'Ø');
        gsmCodes.put("1011000",'+');
        gsmCodes.put("1011001",';');
        gsmCodes.put("1011010",'K');
        gsmCodes.put("1011011",'Ä');
        gsmCodes.put("1011100",'k');
        gsmCodes.put("1011101",'ä');
        gsmCodes.put("1011110",'ø');
        gsmCodes.put("1011111",'Æ');
        gsmCodes.put("1100000",',');
        gsmCodes.put("1100001",'<');
        gsmCodes.put("1100010",'L');
        gsmCodes.put("1100011",'Ö');
        gsmCodes.put("1100100",'l');
        gsmCodes.put("1100101",'ö');
        gsmCodes.put("1100110",'æ');
        gsmCodes.put("1100111",'-');
        gsmCodes.put("1101000",'=');
        gsmCodes.put("1101001",'M');
        gsmCodes.put("1101010",'Ñ');
        gsmCodes.put("1101011",'m');
        gsmCodes.put("1101100",'ñ');
        gsmCodes.put("1101101",'Å');
        gsmCodes.put("1101110",'ß');
        gsmCodes.put("1101111",'.');
        gsmCodes.put("1110000",'>');
        gsmCodes.put("1110001",'N');
        gsmCodes.put("1110010",'Ü');
        gsmCodes.put("1110011",'n');
        gsmCodes.put("1110100",'ü');
        gsmCodes.put("1110101",'å');
        gsmCodes.put("1110110",'É');
        gsmCodes.put("1110111",'/');
        gsmCodes.put("1111000",'?');
        gsmCodes.put("1111001",'O');
        gsmCodes.put("1111010",'§');
        gsmCodes.put("1111011",'o');
        gsmCodes.put("1111100",'à');
        gsmCodes.put("1111101",'@');
        gsmCodes.put("1111110",'\n');//LF\n
        gsmCodes.put("1111111",'€');//ESC char c=0b0011011;
        gsmCodes.put("0000000",'{');//CR\r

    }

    /**
     * Translate Characters of 7 bits ASCII coding into a string of bits
     * Example:
     *     Input( ASCII 7Bits): èèA!
     *     Return (Binary string): 0000100 0000100 1000001 0100001
     * @param input
     * @return
     */
    public String Ascii7BitsStringToStreamOfBits(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {

            String s = this.gsmCodesReversed.get(input.charAt(i));
            if (s == null){
                System.out.println("ERROR wrong code" + input.charAt(i));
                throw new NullPointerException();
            }
            else  result.append(s);
        }

        return result.toString();
    }

    /**
     * Translate a string of bits (0101110101) into 7 bits ASCII encoding characters
     * Example:
     *
     *     Input (String of bits): 0000100 0000100 1000001 0100001
     *     Return (ASCII 7Bits): èèA!
     * @param input
     * @return
     */
    public String stringOfBitsTo7BitsAscii(String input) {
        if (input.length() == 0 || input == null)
            throw new NullPointerException();

        StringBuilder result = new StringBuilder();

        int numberOf7bitsChars = (int) Math.ceil(input.length() / 7.0);

        for (int i = 0; i < numberOf7bitsChars; i++) {
            String SevenBitsChar = null;
            if (input.length() > 7 * i + 7)
                SevenBitsChar = input.substring(7 * i, 7 * i + 7);
            else{
                SevenBitsChar = input.substring(7 * i);
                while (SevenBitsChar.length() < 7)
                    SevenBitsChar+='0';
            }

            //System.out.println("\t c: "+SevenBitsChar);
            Character c = this.gsmCodes.get(SevenBitsChar);
            if (c == null){
                System.out.println("ERROR wrong code" + SevenBitsChar);
                throw new NullPointerException();
            }
            else  result.append((char) c);
        }
        return result.toString();
    }

    /**
     * Read a string of characters and return the number of bits in it
     * to represent the message for GSM sending.
     * Total number of bits possible in one GSM message is 160*8 bits
     * Or 153*8 bits per SMS if divided into multipart SMSes.
     *
     * @param gsmMessage a message to send in an SMS
     * @return number of bits required to send the message
     */
    public int gsmBitsLength(String gsmMessage){
        // return the number of bits used to store the the message as a GSM message

        // only Euro sign is allowed in this extension characters (counted twice)
        // it is equivalent to 14bits in 7bitsASCII

        if (gsmMessage == null || gsmMessage.length() == 0) return 0;

        int numberOf7AsciiChars = 0;
        int numberOf7AsciiExten = 0; //'€' used for ESC or '{' used for \r
        int numberOfOtherChars = 0;

        for (int i = 0; i < gsmMessage.length(); i++){
            Character c = gsmMessage.charAt(i);
            if (c == '€' || c =='|' || c == '^' || c == '{' || c == '}' || c == '[' || c == '~' || c == ']' || c == '\\'){
                numberOf7AsciiExten++;
            }else {
                if (this.gsmCodesReversed.containsKey(c)) {
                    // only 7bits per char (€ already handled)
                    numberOf7AsciiChars++;
                } else {
                    //convert it to unicode message (16 bits per char)
                    numberOfOtherChars++;
                }
            }
        }

        int result = 0;

        if (numberOfOtherChars == 0) result = (numberOf7AsciiChars + numberOf7AsciiExten * 2) * 7;
        else result = (numberOf7AsciiChars + numberOf7AsciiExten + numberOfOtherChars ) * 16;

        return result;
    }


    public int getLimitSMS(int numberOfBits){
        if (numberOfBits <= 160*7) return 160*7; //(1SMS)
        else if (numberOfBits <= 306*7) return 306*7; //(2SMS)
        else if (numberOfBits <= 459*7) return 459*7; //(3SMS)
        else return numberOfBits; // message too long to be sent as an SMS!
    }

    public static void main(String[] args) {

        Ascii7bits gsm = new Ascii7bits();

        int p = 0;
        for (int i=0; i<3; i++){
            p = (int) Math.pow(2, i);
            System.out.println(p);
        }

        String bits = "0101010111000001110000010111111000101011101010110111111111011000000000"
                + "11000000010111111000101011101010110111111111000000000000000001011111100000"
                + "10100000000001000000010111111000101011101010110111111011100000000000000000"
                + "10111111000101011101010110111111111000000011111011100000101111110001010111"
                + "01010110111111111000000000000000001011111100010101110101011011111111100000"
                + "00000011100010101110101011011111111100000111001000100111000000101111110001"
                + "01011101010110111111111000000000000000001011111100010101110101011011111111"
                + "10000000000000000010111111000101011101010110111010111110001011100110101000"
                + "00000000000010111111000101011101010110110101010111111110000001110010101101"
                + "1111000000000010100010111111000101011101010110111111111000000";
        String codes = gsm.stringOfBitsTo7BitsAscii(bits);
        System.out.println("Orginal: "+bits);
        System.out.println("GSMcode: "+codes);
        System.out.println("Reverse: "+gsm.Ascii7BitsStringToStreamOfBits(codes));
    }
}
