/**
 * MIT License
 *
 * Copyright (c) 2010 - 2020 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package oshi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import oshi.util.tuples.Pair;

/**
 * The Class ParseUtilTest.
 */
public class ParseUtilTest {
    /**
     * Test parse hertz.
     */
    @Test
    public void testParseHertz() {
        assertEquals("Failed to parse OneHz", -1L, ParseUtil.parseHertz("OneHz"));
        assertEquals("Failed to parse NotEvenAHertz", -1L, ParseUtil.parseHertz("NotEvenAHertz"));
        assertEquals("Failed to parse 10000000000000000000 Hz", Long.MAX_VALUE,
                ParseUtil.parseHertz("10000000000000000000 Hz"));
        assertEquals("Failed to parse 1Hz", 1L, ParseUtil.parseHertz("1Hz"));
        assertEquals("Failed to parse 500 Hz", 500L, ParseUtil.parseHertz("500 Hz"));
        assertEquals("Failed to parse 1kHz", 1_000L, ParseUtil.parseHertz("1kHz"));
        assertEquals("Failed to parse 1MHz", 1_000_000L, ParseUtil.parseHertz("1MHz"));
        assertEquals("Failed to parse 1GHz", 1_000_000_000L, ParseUtil.parseHertz("1GHz"));
        assertEquals("Failed to parse 1.5GHz", 1_500_000_000L, ParseUtil.parseHertz("1.5GHz"));
        assertEquals("Failed to parse 1THz", 1_000_000_000_000L, ParseUtil.parseHertz("1THz"));
        // GHz exceeds max double
    }

    /**
     * Test parse string.
     */
    @Test
    public void testParseLastInt() {
        assertEquals("Failed to parse -1", -1, ParseUtil.parseLastInt("foo : bar", -1));
        assertEquals("Failed to parse 1", 1, ParseUtil.parseLastInt("foo : 1", 0));
        assertEquals("Failed to parse 2", 2, ParseUtil.parseLastInt("foo", 2));
        assertEquals("Failed to parse 3", 3, ParseUtil.parseLastInt("max_int plus one is 2147483648", 3));
        assertEquals("Failed to parse 255", 255, ParseUtil.parseLastInt("0xff", 4));

        assertEquals("Failed to parse -1 as long", -1L, ParseUtil.parseLastLong("foo : bar", -1L));
        assertEquals("Failed to parse 1 as long", 1L, ParseUtil.parseLastLong("foo : 1", 0L));
        assertEquals("Failed to parse 2 as long", 2L, ParseUtil.parseLastLong("foo", 2L));
        assertEquals("Failed to parse 2147483648L as long", 2147483648L,
                ParseUtil.parseLastLong("max_int plus one is" + " 2147483648", 3L));
        assertEquals("Failed to parse 255 as long", 255L, ParseUtil.parseLastLong("0xff", 0L));

        double epsilon = 1.1102230246251565E-16;
        assertEquals("Failed to parse -1 as double", -1d, ParseUtil.parseLastDouble("foo : bar", -1d), epsilon);
        assertEquals("Failed to parse 1 as double", 1.0, ParseUtil.parseLastDouble("foo : 1.0", 0d), epsilon);
        assertEquals("Failed to parse 2 as double", 2d, ParseUtil.parseLastDouble("foo", 2d), epsilon);
    }

    /**
     * Test parse string.
     */
    @Test
    public void testParseLastString() {
        assertEquals("Failed to parse bar", "bar", ParseUtil.parseLastString("foo : bar"));
        assertEquals("Failed to parse foo", "foo", ParseUtil.parseLastString("foo"));
        assertEquals("Failed to parse \"\"", "", ParseUtil.parseLastString(""));
    }

    /**
     * Test hex string to byte array (and back).
     */
    @Test
    public void testHexStringToByteArray() {
        byte[] temp = { (byte) 0x12, (byte) 0xaf };
        assertTrue(Arrays.equals(temp, ParseUtil.hexStringToByteArray("12af")));
        assertEquals("Failed to parse 12AF", "12AF", ParseUtil.byteArrayToHexString(temp));
        temp = new byte[0];
        assertTrue(Arrays.equals(temp, ParseUtil.hexStringToByteArray("expected error abcde")));
        assertTrue(Arrays.equals(temp, ParseUtil.hexStringToByteArray("abcde")));
    }

    /**
     * Test string to byte array.
     */
    @Test
    public void testStringToByteArray() {
        byte[] temp = { (byte) '1', (byte) '2', (byte) 'a', (byte) 'f', (byte) 0 };
        assertTrue(Arrays.equals(temp, ParseUtil.asciiStringToByteArray("12af", 5)));
    }

    /**
     * Test long to byte array.
     */
    @Test
    public void testLongToByteArray() {
        byte[] temp = { (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0 };
        assertTrue(Arrays.equals(temp, ParseUtil.longToByteArray(0x12345678, 4, 5)));
    }

    /**
     * Test string and byte array to long.
     */
    @Test
    public void testStringAndByteArrayToLong() {
        byte[] temp = { (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e' };
        long abcde = (long) temp[0] << 32 | temp[1] << 24 | temp[2] << 16 | temp[3] << 8 | temp[4];
        // Test string
        assertEquals("Failed to parse \"abcde\"", abcde, ParseUtil.strToLong("abcde", 5));
        // Test byte array
        assertEquals("Failed to parse " + abcde, abcde, ParseUtil.byteArrayToLong(temp, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testByteArrayToLongSizeTooBig() {
        ParseUtil.byteArrayToLong(new byte[10], 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testByteArrayToLongSizeBigger() {
        ParseUtil.byteArrayToLong(new byte[7], 8);
    }

    /**
     * Test byte arry to float
     */
    @Test
    public void testByteArrayToFloat() {
        byte[] temp = { (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a };
        float f = (temp[0] << 22 | temp[1] << 14 | temp[2] << 6 | temp[3] >>> 2) + (float) (temp[3] & 0x3) / 0x4;
        assertEquals(f, ParseUtil.byteArrayToFloat(temp, 4, 2), Float.MIN_VALUE);
        f = 0x12345 + (float) 0x6 / 0x10;
        assertEquals(f, ParseUtil.byteArrayToFloat(temp, 3, 4), Float.MIN_VALUE);
        f = 0x123 + (float) 0x4 / 0x10;
        assertEquals(f, ParseUtil.byteArrayToFloat(temp, 2, 4), Float.MIN_VALUE);
    }

    /**
     * Test unsigned int to long
     */
    @Test
    public void testUnsignedIntToLong() {
        assertEquals("Failed to parse 0 as long", 0L, ParseUtil.unsignedIntToLong(0));
        assertEquals("Failed to parse 123 as long", 123L, ParseUtil.unsignedIntToLong(123));
        assertEquals("Failed to parse 4294967295L as long", 4294967295L, ParseUtil.unsignedIntToLong(0xffffffff));
    }

    /**
     * Test unsigned long to signed long
     */
    @Test
    public void testUnsignedLongToSignedLong() {
        assertEquals("Failed to parse 1 as signed long", 1L, ParseUtil.unsignedLongToSignedLong(Long.MAX_VALUE + 2));
        assertEquals("Failed to parse 123 as signed long", 123L, ParseUtil.unsignedLongToSignedLong(123));
        assertEquals("Failed to parse 9223372036854775807 as signed long", 9223372036854775807L,
                ParseUtil.unsignedLongToSignedLong(9223372036854775807L));
    }

    /**
     * Test hex string to string
     */
    @Test
    public void testHexStringToString() {
        assertEquals("Failed to parse ABC as string", "ABC", ParseUtil.hexStringToString("414243"));
        assertEquals("Failed to parse ab00cd as string", "ab00cd", ParseUtil.hexStringToString("ab00cd"));
        assertEquals("Failed to parse ab88cd as string", "ab88cd", ParseUtil.hexStringToString("ab88cd"));
        assertEquals("Failed to parse notHex as string", "notHex", ParseUtil.hexStringToString("notHex"));
        assertEquals("Failed to parse 320 as string", "320", ParseUtil.hexStringToString("320"));
        assertEquals("Failed to parse 0 as string", "0", ParseUtil.hexStringToString("0"));
    }

    /**
     * Test parse int
     */
    @Test
    public void testParseIntOrDefault() {
        assertEquals("Failed to parse 123", 123, ParseUtil.parseIntOrDefault("123", 45));
        assertEquals("Failed to parse 45", 45, ParseUtil.parseIntOrDefault("123X", 45));
    }

    /**
     * Test parse long
     */
    @Test
    public void testParseLongOrDefault() {
        assertEquals("Failed to parse 123", 123L, ParseUtil.parseLongOrDefault("123", 45L));
        assertEquals("Failed to parse 45", 45L, ParseUtil.parseLongOrDefault("123L", 45L));
    }

    /**
     * Test parse long
     */
    @Test
    public void testParseUnsignedLongOrDefault() {
        assertEquals("Failed to parse 9223372036854775807L", 9223372036854775807L,
                ParseUtil.parseUnsignedLongOrDefault("9223372036854775807", 123L));
        assertEquals("Failed to parse 9223372036854775808L", -9223372036854775808L,
                ParseUtil.parseUnsignedLongOrDefault("9223372036854775808", 45L));
        assertEquals("Failed to parse 1L", -1L, ParseUtil.parseUnsignedLongOrDefault("18446744073709551615", 123L));
        assertEquals("Failed to parse 0L", 0L, ParseUtil.parseUnsignedLongOrDefault("18446744073709551616", 45L));
        assertEquals("Failed to parse 123L", 123L, ParseUtil.parseUnsignedLongOrDefault("9223372036854775808L", 123L));
    }

    /**
     * Test parse double
     */
    @Test
    public void testParseDoubleOrDefault() {
        assertEquals("Failed to parse 1.23d", 1.23d, ParseUtil.parseDoubleOrDefault("1.23", 4.5d), Double.MIN_VALUE);
        assertEquals("Failed to parse 4.5d", 4.5d, ParseUtil.parseDoubleOrDefault("one.twentythree", 4.5d),
                Double.MIN_VALUE);
    }

    /**
     * Test parse DHMS
     */
    @Test
    public void testParseDHMSOrDefault() {
        assertEquals("Failed to parse 93784050L", 93784050L, ParseUtil.parseDHMSOrDefault("1-02:03:04.05", 0L));
        assertEquals("Failed to parse 93784000L", 93784000L, ParseUtil.parseDHMSOrDefault("1-02:03:04", 0L));
        assertEquals("Failed to parse 7384000L", 7384000L, ParseUtil.parseDHMSOrDefault("02:03:04", 0L));
        assertEquals("Failed to parse 184050L", 184050L, ParseUtil.parseDHMSOrDefault("03:04.05", 0L));
        assertEquals("Failed to parse 184000L", 184000L, ParseUtil.parseDHMSOrDefault("03:04", 0L));
        assertEquals("Failed to parse 4000L", 4000L, ParseUtil.parseDHMSOrDefault("04", 0L));
        assertEquals("Failed to parse 0L", 0L, ParseUtil.parseDHMSOrDefault("04:05-06", 0L));
    }

    /**
     * Test parse UUID
     */
    @Test
    public void testParseUuidOrDefault() {
        assertEquals("Failed to parse 123e4567-e89b-12d3-a456-426655440000", "123e4567-e89b-12d3-a456-426655440000",
                ParseUtil.parseUuidOrDefault("123e4567-e89b-12d3-a456-426655440000", "default"));
        assertEquals("Failed to parse 123e4567-e89b-12d3-a456-426655440000", "123e4567-e89b-12d3-a456-426655440000",
                ParseUtil.parseUuidOrDefault("The UUID is 123E4567-E89B-12D3-A456-426655440000!", "default"));
        assertEquals("Failed to parse foo or default", "default", ParseUtil.parseUuidOrDefault("foo", "default"));
    }

    /**
     * Test parse SingleQuoteString
     */
    @Test
    public void testGetSingleQuoteStringValue() {
        assertEquals("Failed to parse bar", "bar", ParseUtil.getSingleQuoteStringValue("foo = 'bar' (string)"));
        assertEquals("Failed to parse empty string", "", ParseUtil.getSingleQuoteStringValue("foo = bar (string)"));
    }

    @Test
    public void testGetDoubleQuoteStringValue() {
        assertEquals("Failed to parse bar", "bar", ParseUtil.getDoubleQuoteStringValue("foo = \"bar\" (string)"));
        assertEquals("Failed to parse empty string", "", ParseUtil.getDoubleQuoteStringValue("hello"));
    }

    /**
     * Test parse SingleQuoteBetweenMultipleQuotes
     */
    @Test
    public void testGetStringBetweenMultipleQuotes() {
        assertEquals("Failed to parse Single quotes between Multiple quotes", "hello $ is",
                ParseUtil.getStringBetween("hello = $hello $ is $", '$'));
        assertEquals("Failed to parse Single quotes between Multiple quotes", "Realtek AC'97 Audio",
                ParseUtil.getStringBetween("pci.device = 'Realtek AC'97 Audio'", '\''));
    }

    /**
     * Test parse FirstIntValue
     */
    @Test
    public void testGetFirstIntValue() {
        assertEquals("Failed to parse FirstIntValue", 42, ParseUtil.getFirstIntValue("foo = 42 (0x2a) (int)"));
        assertEquals("Failed to parse FirstIntValue", 0, ParseUtil.getFirstIntValue("foo = 0x2a (int)"));
        assertEquals("Failed to parse FirstIntValue", 42, ParseUtil.getFirstIntValue("42"));
        assertEquals("Failed to parse FirstIntValue", 10, ParseUtil.getFirstIntValue("10.12.2"));
    }

    /**
     * Test parse NthIntValue
     */
    @Test
    public void testGetNthIntValue() {
        assertEquals("Failed to parse NthIntValue", 2, ParseUtil.getNthIntValue("foo = 42 (0x2a) (int)", 3));
        assertEquals("Failed to parse NthIntValue", 0, ParseUtil.getNthIntValue("foo = 0x2a (int)", 3));
        assertEquals("Failed to parse NthIntValue", 12, ParseUtil.getNthIntValue("10.12.2", 2));
    }

    /**
     * Test parse removeMatchingString
     */
    @Test
    public void testRemoveMatchingString() {
        assertEquals("Failed to parse removeMatchingString", "foo = 42 () (int)",
                ParseUtil.removeMatchingString("foo = 42 (0x2a) (int)", "0x2a"));
        assertEquals("Failed to parse removeMatchingString", "foo = 0x2a (int)",
                ParseUtil.removeMatchingString("foo = 0x2a (int)", "qqq"));
        assertEquals("Failed to parse removeMatchingString", "10.1.", ParseUtil.removeMatchingString("10.12.2", "2"));
        assertEquals("Failed to parse removeMatchingString", "", ParseUtil.removeMatchingString("10.12.2", "10.12.2"));
        assertEquals("Failed to parse removeMatchingString", "", ParseUtil.removeMatchingString("", "10.12.2"));
        assertEquals("Failed to parse removeMatchingString", null, ParseUtil.removeMatchingString(null, "10.12.2"));
        assertEquals("Failed to parse removeMatchingString", "2", ParseUtil.removeMatchingString("10.12.2", "10.12."));
    }

    /**
     * Test parse string to array
     */
    @Test
    public void testParseStringToLongArray() {
        int[] indices = { 1, 3 };
        long now = System.currentTimeMillis();

        String foo = String.format("The numbers are %d %d %d %d", 123, 456, 789, now);
        int count = ParseUtil.countStringToLongArray(foo, ' ');
        assertEquals("countStringToLongArray should return 4 for \"" + foo + "\"", 4, count);
        long[] result = ParseUtil.parseStringToLongArray(foo, indices, 4, ' ');
        assertEquals("result[0] should be 456 using parseStringToLongArray on \"" + foo + "\"", 456L, result[0]);
        assertEquals("result[1] should be " + now + " using parseStringToLongArray on \"" + foo + "\"", now, result[1]);

        foo = String.format("The numbers are %d %d %d %d %s", 123, 456, 789, now,
                "709af748-5f8e-41b3-b73a-b440ef4406c8");
        count = ParseUtil.countStringToLongArray(foo, ' ');
        assertEquals("countStringToLongArray should return 4 for \"" + foo + "\"", 4, count);
        result = ParseUtil.parseStringToLongArray(foo, indices, 4, ' ');
        assertEquals("result[0] should be 456 using parseStringToLongArray on \"" + foo + "\"", 456L, result[0]);
        assertEquals("result[1] should be " + now + " using parseStringToLongArray on \"" + foo + "\"", now, result[1]);

        foo = String.format("The numbers are %d -%d %d +%d", 123, 456, 789, now);
        count = ParseUtil.countStringToLongArray(foo, ' ');
        assertEquals("countStringToLongArray should return 4 for \"" + foo + "\"", 4, count);
        result = ParseUtil.parseStringToLongArray(foo, indices, 4, ' ');
        assertEquals("result[0] should be -4456 using parseStringToLongArray on \"" + foo + "\"", -456L, result[0]);
        assertEquals("result[1] index should be 456 using parseStringToLongArray on \"" + foo + "\"", now, result[1]);

        foo = String.format("Invalid character %d %s %d %d", 123, "4v6", 789, now);
        count = ParseUtil.countStringToLongArray(foo, ' ');
        assertEquals("countStringToLongArray should return 2 for \"" + foo + "\"", 2, count);
        result = ParseUtil.parseStringToLongArray(foo, indices, 4, ' ');
        assertEquals("result[1] index should be 0 using parseStringToLongArray on \"" + foo + "\"", 0, result[1]);

        foo = String.format("Exceeds max long %d %d %d 1%d", 123, 456, 789, Long.MAX_VALUE);
        result = ParseUtil.parseStringToLongArray(foo, indices, 4, ' ');
        assertEquals("result[1] index should be " + Long.MAX_VALUE
                + " (Long.MAX_VALUE) using parseStringToLongArray on \"" + foo + "\"", Long.MAX_VALUE, result[1]);

        foo = String.format("String too short %d %d %d %d", 123, 456, 789, now);
        result = ParseUtil.parseStringToLongArray(foo, indices, 9, ' ');
        assertEquals("result[1] index should be 0 using parseStringToLongArray on \"" + foo + "\"", 0, result[1]);

        foo = String.format("Array too short %d %d %d %d", 123, 456, 789, now);
        result = ParseUtil.parseStringToLongArray(foo, indices, 2, ' ');
        assertEquals("result[1] index should be 0 using parseStringToLongArray on \"" + foo + "\"", 0, result[1]);

        foo = String.format("%d %d %d %d", 123, 456, 789, now);
        count = ParseUtil.countStringToLongArray(foo, ' ');
        assertEquals("countStringToLongArray should return 4 for \"" + foo + "\"", 4, count);

        foo = String.format("%d %d %d %d nonNumeric", 123, 456, 789, now);
        count = ParseUtil.countStringToLongArray(foo, ' ');
        assertEquals("countStringToLongArray should return 4 for \"" + foo + "\"", 4, count);

        foo = String.format("%d %d %d %d 123-456", 123, 456, 789, now);
        count = ParseUtil.countStringToLongArray(foo, ' ');
        assertEquals("countStringToLongArray should return 4 for \"" + foo + "\"", 4, count);
    }

    @Test
    public void testTextBetween() {
        String text = "foo bar baz";
        String before = "foo";
        String after = "baz";
        assertEquals(" bar ", ParseUtil.getTextBetweenStrings(text, before, after));

        before = "";
        assertEquals("foo bar ", ParseUtil.getTextBetweenStrings(text, before, after));

        before = "food";
        assertEquals("", ParseUtil.getTextBetweenStrings(text, before, after));

        before = "foo";
        after = "qux";
        assertEquals("", ParseUtil.getTextBetweenStrings(text, before, after));

    }

    @Test
    public void testFiletimeToMs() {
        assertEquals(1172163600306L, ParseUtil.filetimeToUtcMs(128166372003061629L, false));
    }

    @Test
    public void testParseCimDateTimeToOffset() {
        String cimDateTime = "20160513072950.782000-420";
        // 2016-05-13T07:29:50 == 1463124590
        // Add 420 minutes to get unix seconds
        Instant timeInst = Instant.ofEpochMilli(1463124590_782L + 60 * 420_000L);
        assertEquals(timeInst, ParseUtil.parseCimDateTimeToOffset(cimDateTime).toInstant());
        assertEquals(Instant.EPOCH, ParseUtil.parseCimDateTimeToOffset("Not a datetime").toInstant());
    }

    @Test
    public void testFilePathStartsWith() {
        List<String> prefixList = Arrays.asList("/foo", "/bar");
        assertEquals(true, ParseUtil.filePathStartsWith(prefixList, "/foo"));
        assertEquals(true, ParseUtil.filePathStartsWith(prefixList, "/foo/bar"));
        assertEquals(false, ParseUtil.filePathStartsWith(prefixList, "/foobar"));
        assertEquals(true, ParseUtil.filePathStartsWith(prefixList, "/foo/baz"));
        assertEquals(false, ParseUtil.filePathStartsWith(prefixList, "/baz/foo"));
    }

    @Test
    public void testParseDecimalMemorySizeToBinary() {
        assertEquals(0, ParseUtil.parseDecimalMemorySizeToBinary("Not a number"));
        assertEquals(1, ParseUtil.parseDecimalMemorySizeToBinary("1"));
        assertEquals(1024, ParseUtil.parseDecimalMemorySizeToBinary("1 kB"));
        assertEquals(1024, ParseUtil.parseDecimalMemorySizeToBinary("1 KB"));
        assertEquals(1_048_576, ParseUtil.parseDecimalMemorySizeToBinary("1 MB"));
        assertEquals(1_048_576, ParseUtil.parseDecimalMemorySizeToBinary("1MB"));
        assertEquals(1_073_741_824, ParseUtil.parseDecimalMemorySizeToBinary("1 GB"));
        assertEquals(1_099_511_627_776L, ParseUtil.parseDecimalMemorySizeToBinary("1 TB"));
    }

    @Test
    public void testParsePnPDeviceIdToVendorProductId() {
        Pair<String, String> idPair = ParseUtil
                .parsePnPDeviceIdToVendorProductId("PCI\\VEN_10DE&DEV_134B&SUBSYS_00081414&REV_A2\\4&25BACB6&0&00E0");
        assertNotNull(idPair);
        assertEquals("First element of pair mismatch.","0x10de", idPair.getA());
        assertEquals("Second element of pair mismatch.","0x134b", idPair.getB());

        idPair = ParseUtil
                .parsePnPDeviceIdToVendorProductId("PCI\\VEN_80286&DEV_19116&SUBSYS_00141414&REV_07\\3&11583659&0&10");
        assertNull(idPair);
    }

    @Test
    public void testParseLshwResourceString() {
        assertEquals(268_435_456L + 65_536L, ParseUtil.parseLshwResourceString(
                "irq:46 ioport:6000(size=32) memory:b0000000-bfffffff memory:e2000000-e200ffff"));
        assertEquals(268_435_456L, ParseUtil.parseLshwResourceString(
                "irq:46 ioport:6000(size=32) memory:b0000000-bfffffff memory:x2000000-e200ffff"));
        assertEquals(65_536L, ParseUtil.parseLshwResourceString(
                "irq:46 ioport:6000(size=32) memory:x0000000-bfffffff memory:e2000000-e200ffff"));
        assertEquals(0, ParseUtil.parseLshwResourceString("some random string"));
    }

    @Test
    public void testParseLspciMachineReadable() {
        Pair<String, String> pair = ParseUtil.parseLspciMachineReadable("foo [bar]");
        assertEquals("First element of pair mismatch.", "foo", pair.getA());
        assertEquals("Second element of pair mismatch.","bar", pair.getB());
        assertNull(ParseUtil.parseLspciMachineReadable("Bad format"));
    }

    @Test
    public void testParseLspciMemorySize() {
        assertEquals(0, ParseUtil.parseLspciMemorySize("Doesn't parse"));
        assertEquals(64 * 1024, ParseUtil.parseLspciMemorySize("Foo [size=64K]"));
        assertEquals(256 * 1024 * 1024, ParseUtil.parseLspciMemorySize("Foo [size=256M]"));
    }

    @Test
    public void testParseHyphenatedIntList() {
        String s = "1";
        List<Integer> parsed = ParseUtil.parseHyphenatedIntList(s);
        assertFalse(parsed.contains(0));
        assertTrue(parsed.contains(1));

        s = "0 2-5 7";
        parsed = ParseUtil.parseHyphenatedIntList(s);
        assertTrue(parsed.contains(0));
        assertFalse(parsed.contains(1));
        assertTrue(parsed.contains(2));
        assertTrue(parsed.contains(3));
        assertTrue(parsed.contains(4));
        assertTrue(parsed.contains(5));
        assertFalse(parsed.contains(6));
        assertTrue(parsed.contains(7));
    }

    @Test
    public void testParseMmDdYyyyToYyyyMmDD() {
        assertEquals("Unable to parse MM-DD-YYYY date string into YYYY-MM-DD date string", "2222-00-11",
                ParseUtil.parseMmDdYyyyToYyyyMmDD("00-11-2222"));
        assertEquals("Date string should not be parsed", "badstr", ParseUtil.parseMmDdYyyyToYyyyMmDD("badstr"));
    }

    @Test
    public void testParseUtAddrV6toIP() {
        int[] zero = { 0, 0, 0, 0 };
        int[] loopback = { 0, 0, 0, 1 };
        String v6test = "2001:db8:85a3::8a2e:370:7334";
        int[] v6 = new int[4];
        v6[0] = Integer.parseUnsignedInt("20010db8", 16);
        v6[1] = Integer.parseUnsignedInt("85a30000", 16);
        v6[2] = Integer.parseUnsignedInt("00008a2e", 16);
        v6[3] = Integer.parseUnsignedInt("03707334", 16);
        String v4test = "127.0.0.1";
        int[] v4 = new int[4];
        v4[0] = (127 << 24) + 1;
        assertEquals("Unspecified address failed", "::", ParseUtil.parseUtAddrV6toIP(zero));
        assertEquals("Loopback address failed", "::1", ParseUtil.parseUtAddrV6toIP(loopback));
        assertEquals("V6 parsing failed", v6test, ParseUtil.parseUtAddrV6toIP(v6));
        assertEquals("V4 parsig failed", v4test, ParseUtil.parseUtAddrV6toIP(v4));
    }

    @Test
    public void testHexStringToLong() {
        assertEquals(255L, ParseUtil.hexStringToLong("ff", 0L));
        assertEquals(-2096147552L, ParseUtil.hexStringToLong("ffffffff830f53a0", 0L));
        assertEquals(0L, ParseUtil.hexStringToLong("pqwe", 0L));
    }

    @Test
    public void testRemoveLeadingDots() {
        assertEquals("foo", ParseUtil.removeLeadingDots("foo"));
        assertEquals("bar", ParseUtil.removeLeadingDots("...bar"));
        assertEquals("", ParseUtil.removeLeadingDots("..."));
    }
}
