package com.qrcodeapi.qrcodeapi.ultls;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QrCode {

    private final int[][] matrix;

    public QrCode(String data) {
        int correctLevel = 2;
        QRCodeModel qrCodeModel = new QRCodeModel(_getTypeNumber(data, correctLevel), correctLevel);
        qrCodeModel.addData(data);
        qrCodeModel.make();
        this.matrix = qrCodeModel.modules;
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    private final int[][] QRCodeLimitLength = new int[][]{{17, 14, 11, 7}, {32, 26, 20, 14}, {53, 42, 32, 24}, {78, 62, 46, 34}, {106, 84, 60, 44}, {134, 106, 74, 58}, {154, 122, 86, 64}, {192, 152, 108, 84}, {230, 180, 130, 98}, {271, 213, 151, 119}, {321, 251, 177, 137}, {367, 287, 203, 155}, {425, 331, 241, 177}, {458, 362, 258, 194}, {520, 412, 292, 220}, {586, 450, 322, 250}, {644, 504, 364, 280}, {718, 560, 394, 310}, {792, 624, 442, 338}, {858, 666, 482, 382}, {929, 711, 509, 403}, {1003, 779, 565, 439}, {1091, 857, 611, 461}, {1171, 911, 661, 511}, {1273, 997, 715, 535}, {1367, 1059, 751, 593}, {1465, 1125, 805, 625}, {1528, 1190, 868, 658}, {1628, 1264, 908, 698}, {1732, 1370, 982, 742}, {1840, 1452, 1030, 790}, {1952, 1538, 1112, 842}, {2068, 1628, 1168, 898}, {2188, 1722, 1228, 958}, {2303, 1809, 1283, 983}, {2431, 1911, 1351, 1051}, {2563, 1989, 1423, 1093}, {2699, 2099, 1499, 1139}, {2809, 2213, 1579, 1219}, {2953, 2331, 1663, 1273}};

    private static class QRMode {
        private final static int MODE_NUMBER = 1;
        private final static int MODE_ALPHA_NUM = 1 << 1;
        private final static int MODE_8BIT_BYTE = 1 << 2;
        private final static int MODE_KANJI = 1 << 3;

    }

    private static class QRErrorCorrectLevel {
        private final static int L = 1;
        private final static int M = 0;
        private final static int Q = 3;
        private final static int H = 2;
    }

    private static class QRMaskPattern {
        private final static int PATTERN000 = 0;
        private final static int PATTERN001 = 1;
        private final static int PATTERN010 = 2;
        private final static int PATTERN011 = 3;
        private final static int PATTERN100 = 4;
        private final static int PATTERN101 = 5;
        private final static int PATTERN110 = 6;
        private final static int PATTERN111 = 7;
    }

    private static class QR8bitByte {
        private final int mode = QRMode.MODE_8BIT_BYTE;
        private final List<Integer> parsedData = new ArrayList<>();

        public QR8bitByte(String data) {

            int i = 0;
            for (var l = data.length(); i < l; i++) {
                var code = data.codePointAt(i);
                List<Integer> byteArray = new ArrayList<>();

                if (code > 0x10000) {
                    byteArray.add(0xF0 | ((code & 0x1C0000) >>> 18));
                    byteArray.add(0x80 | ((code & 0x3F000) >>> 12));
                    byteArray.add(0x80 | ((code & 0xFC0) >>> 6));
                    byteArray.add(0x80 | (code & 0x3F));
                } else if (code > 0x800) {
                    byteArray.add(0xE0 | ((code & 0xF000) >>> 12));
                    byteArray.add(0x80 | ((code & 0xFC0) >>> 6));
                    byteArray.add(0x80 | (code & 0x3F));
                } else if (code > 0x80) {
                    byteArray.add(0xC0 | ((code & 0x7C0) >>> 6));
                    byteArray.add(0x80 | (code & 0x3F));
                } else {
                    byteArray.add(code);
                }

                this.parsedData.addAll(byteArray);
            }

            if (this.parsedData.size() != data.length()) {
                this.parsedData.add(0, 191);
                this.parsedData.add(0, 187);
                this.parsedData.add(0, 239);
            }

        }

        public int getLength() {
            return this.parsedData.size();
        }

        public void write(QRBitBuffer buffer) {
            var i = 0;
            for (var l = this.parsedData.size(); i < l; i++) {
                buffer.put(this.parsedData.get(i), 8);
            }
        }
    }

    private int _getUTF8Length(String a) {
        String encodedA;
        try {
            encodedA = URLEncoder.encode(a, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("UTF-8 no soportado", e);
        }
        String b = encodedA.replaceAll("%[0-9a-fA-F]{2}", "a");
        int bLength = b.length();
        int extra = bLength != a.length() ? 3 : 0;

        return bLength + extra;
    }

    private int _getTypeNumber(String sText, int nCorrectLevel) {
        var nType = 1;
        var length = _getUTF8Length(sText);

        var i = 0;
        for (var len = QRCodeLimitLength.length; i <= len; i++) {
            var nLimit = switch (nCorrectLevel) {
                case QRErrorCorrectLevel.L -> QRCodeLimitLength[i][0];
                case QRErrorCorrectLevel.M -> QRCodeLimitLength[i][1];
                case QRErrorCorrectLevel.Q -> QRCodeLimitLength[i][2];
                case QRErrorCorrectLevel.H -> QRCodeLimitLength[i][3];
                default -> 0;
            };

            if (length <= nLimit) {
                break;
            } else {
                nType++;
            }
        }

        if (nType > QRCodeLimitLength.length) {
            throw new RuntimeException("Too long data");
        }

        return nType;

    }

    private static class QRCodeModel {
        private final int typeNumber;
        private final int errorCorrectLevel;
        private int[][] modules = null;
        private int[] dataCache = null;
        private int moduleCount = 0;
        private final List<QR8bitByte> dataList = new ArrayList<>();

        public QRCodeModel(int typeNumber, int errorCorrectLevel) {
            this.typeNumber = typeNumber;
            this.errorCorrectLevel = errorCorrectLevel;

            for (int h = 0; 8 > h; h++) {
                QRMath.EXP_TABLE[h] = 1 << h;
            }

            for (var h = 8; 256 > h; h++) {
                QRMath.EXP_TABLE[h] = QRMath.EXP_TABLE[h - 4] ^ QRMath.EXP_TABLE[h - 5] ^ QRMath.EXP_TABLE[h - 6] ^ QRMath.EXP_TABLE[h - 8];
            }

            for (var h = 0; 255 > h; h++) {
                QRMath.LOG_TABLE[QRMath.EXP_TABLE[h]] = h;
            }

        }

        public void addData(String data) {
            var newData = new QR8bitByte(data);
            this.dataList.add(newData);
            this.dataCache = null;

        }

        public int isDark(int row, int col) {
            if (row < 0 || this.moduleCount <= row || col < 0 || this.moduleCount <= col) {
                throw new RuntimeException(row + "," + col);
            }
            return this.modules[row][col];
        }

        private int getModuleCount() {
            return this.moduleCount;
        }

        public void make() {
            this.makeImpl(false, this.getBestMaskPattern());
        }

        public void makeImpl(boolean test, int maskPattern) {
            this.moduleCount = this.typeNumber * 4 + 17;
            this.modules = new int[this.moduleCount][0];

            for (var row = 0; row < this.moduleCount; row++) {
                this.modules[row] = new int[this.moduleCount];
                for (var col = 0; col < this.moduleCount; col++) {
                    this.modules[row][col] = -1;
                }
            }

            this.setupPositionProbePattern(0, 0);
            this.setupPositionProbePattern(this.moduleCount - 7, 0);
            this.setupPositionProbePattern(0, this.moduleCount - 7);
            this.setupPositionAdjustPattern();
            this.setupTimingPattern();
            this.setupTypeInfo(test, maskPattern);

            if (this.typeNumber >= 7) {
                this.setupTypeNumber(test);
            }

            if (this.dataCache == null) {
                this.dataCache = QRCodeModel.createData(this.typeNumber, this.errorCorrectLevel, this.dataList);
            }

            this.mapData(this.dataCache, maskPattern);

        }

        public static int[] createData(int typeNumber, int errorCorrectLevel, List<QR8bitByte> dataList) {
            var rsBlocks = QRRSBlock.getRSBlocks(typeNumber, errorCorrectLevel);
            var buffer = new QRBitBuffer();

            for (QR8bitByte data : dataList) {
                buffer.put(data.mode, 4);
                buffer.put(data.getLength(), QRUtil.getLengthInBits(data.mode, typeNumber));
                data.write(buffer);
            }

            var totalDataCount = 0;
            for (QRRSBlock rsBlock : rsBlocks) {
                totalDataCount += rsBlock.dataCount;
            }

            if (buffer.getLengthInBits() > totalDataCount * 8) {
                throw new RuntimeException("code length overflow. (" + buffer.getLengthInBits() + ">" + totalDataCount * 8 + ")");
            }

            if (buffer.getLengthInBits() + 4 <= totalDataCount * 8) {
                buffer.put(0, 4);
            }

            while (buffer.getLengthInBits() % 8 != 0) {
                buffer.putBit(false);
            }

            while (true) {
                if (buffer.getLengthInBits() >= totalDataCount * 8) {
                    break;
                }
                buffer.put(0xEC, 8);
                if (buffer.getLengthInBits() >= totalDataCount * 8) {
                    break;
                }
                buffer.put(0x11, 8);
            }

            return createBytes(buffer, rsBlocks);
        }

        public static int[] createBytes(QRBitBuffer buffer, List<QRRSBlock> rsBlocks) {

            var offset = 0;
            var maxDcCount = 0;
            var maxEcCount = 0;
            var dcdata = new int[rsBlocks.size()][0];
            var ecdata = new int[rsBlocks.size()][0];
            for (var r = 0; r < rsBlocks.size(); r++) {
                var dcCount = rsBlocks.get(r).dataCount;
                var ecCount = rsBlocks.get(r).totalCount - dcCount;
                maxDcCount = Math.max(maxDcCount, dcCount);
                maxEcCount = Math.max(maxEcCount, ecCount);
                dcdata[r] = new int[dcCount];
                for (var i = 0; i < dcdata[r].length; i++) {
                    dcdata[r][i] = 0xff & buffer.buffer[i + offset];
                }
                offset += dcCount;
                var rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount);
                var rawPoly = new QRPolynomial(dcdata[r], rsPoly.getLength() - 1);
                var modPoly = rawPoly.mod(rsPoly);
                ecdata[r] = new int[rsPoly.getLength() - 1];
                for (var i = 0; i < ecdata[r].length; i++) {
                    var modIndex = i + modPoly.getLength() - ecdata[r].length;
                    ecdata[r][i] = (modIndex >= 0) ? modPoly.get(modIndex) : 0;
                }
            }
            var totalCodeCount = 0;
            for (QRRSBlock rsBlock : rsBlocks) {
                totalCodeCount += rsBlock.totalCount;
            }
            var data = new int[totalCodeCount];
            var index = 0;
            for (var i = 0; i < maxDcCount; i++) {
                for (var r = 0; r < rsBlocks.size(); r++) {
                    if (i < dcdata[r].length) {
                        data[index++] = dcdata[r][i];
                    }
                }
            }
            for (var i = 0; i < maxEcCount; i++) {
                for (var r = 0; r < rsBlocks.size(); r++) {
                    if (i < ecdata[r].length) {
                        data[index++] = ecdata[r][i];
                    }
                }
            }
            return data;
        }

        public void setupPositionProbePattern(int row, int col) {

            for (var r = -1; r <= 7; r++) {
                if (row + r <= -1 || this.moduleCount <= row + r) {
                    continue;
                }

                for (var c = -1; c <= 7; c++) {
                    if (col + c <= -1 || this.moduleCount <= col + c) {
                        continue;
                    }

                    if ((0 <= r && r <= 6 && (c == 0 || c == 6)) || (0 <= c && c <= 6 && (r == 0 || r == 6)) || (2 <= r && r <= 4 && 2 <= c && c <= 4)) {

                        if (c == 0 || c == 6 || r == 0 || r == 6) {
                            this.modules[row + r][col + c] = 2;
                        } else {
                            this.modules[row + r][col + c] = 1;
                        }

                    } else {
                        this.modules[row + r][col + c] = 0;
                    }
                }
            }

        }

        public void setupTimingPattern() {
            for (var r = 8; r < this.moduleCount - 8; r++) {
                if (this.modules[r][6] != -1) {
                    continue;
                }
                this.modules[r][6] = (r % 2 == 0) ? 3 : 0;
            }
            for (var c = 8; c < this.moduleCount - 8; c++) {
                if (this.modules[6][c] != -1) {
                    continue;
                }
                this.modules[6][c] = (c % 2 == 0) ? 3 : 0;
            }

        }

        public void setupPositionAdjustPattern() {
            var pos = QRUtil.getPatternPosition(this.typeNumber);
            for (int po : pos) {
                for (int i : pos) {
                    if (this.modules[po][i] != -1) {
                        continue;
                    }
                    for (var r = -2; r <= 2; r++) {
                        for (var c = -2; c <= 2; c++) {
                            if (r == -2 || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0)) {
                                this.modules[po + r][i + c] = 4;
                            } else {
                                this.modules[po + r][i + c] = 0;
                            }
                        }
                    }
                }
            }
        }

        public void setupTypeNumber(boolean test) {
            var bits = QRUtil.getBCHTypeNumber(this.typeNumber);
            for (var i = 0; i < 18; i++) {
                var mod = (!test && ((bits >> i) & 1) == 1);
                this.modules[(int) Math.floor((double) i / 3)][i % 3 + this.moduleCount - 8 - 3] = mod ? 5 : 0;
            }
            for (var i = 0; i < 18; i++) {
                var mod = (!test && ((bits >> i) & 1) == 1);
                this.modules[i % 3 + this.moduleCount - 8 - 3][(int) Math.floor((double) i / 3)] = mod ? 5 : 0;
            }

        }

        public void setupTypeInfo(boolean test, int maskPattern) {
            var data = (this.errorCorrectLevel << 3) | maskPattern;
            var bits = QRUtil.getBCHTypeInfo(data);
            for (var i = 0; i < 15; i++) {
                var mod = (!test && ((bits >> i) & 1) == 1);
                if (i < 6) {
                    this.modules[i][8] = mod ? 6 : 0;
                } else if (i < 8) {
                    this.modules[i + 1][8] = mod ? 6 : 0;
                } else {
                    this.modules[this.moduleCount - 15 + i][8] = mod ? 6 : 0;
                }
            }
            for (var i = 0; i < 15; i++) {
                var mod = (!test && ((bits >> i) & 1) == 1);
                if (i < 8) {
                    this.modules[8][this.moduleCount - i - 1] = mod ? 6 : 0;
                } else if (i < 9) {
                    this.modules[8][15 - i - 1 + 1] = mod ? 6 : 0;
                } else {
                    this.modules[8][15 - i - 1] = mod ? 6 : 0;
                }
            }
            this.modules[this.moduleCount - 8][8] = (!test) ? 0 : 6;
        }

        public int getBestMaskPattern() {
            var minLostPoint = 0;
            var pattern = 0;
            for (var i = 0; i < 8; i++) {
                this.makeImpl(true, i);
                var lostPoint = QRUtil.getLostPoint(this);
                if (i == 0 || minLostPoint > lostPoint) {
                    minLostPoint = lostPoint;
                    pattern = i;
                }
            }
            return pattern;

        }

        public void mapData(int[] data, int maskPattern) {
            var inc = -1;
            var row = this.moduleCount - 1;
            var bitIndex = 7;
            var byteIndex = 0;
            for (var col = this.moduleCount - 1; col > 0; col -= 2) {
                if (col == 6) col--;
                while (true) {
                    for (var c = 0; c < 2; c++) {
                        if (this.modules[row][col - c] == -1) {
                            var dark = false;
                            if (byteIndex < data.length) {
                                dark = (((data[byteIndex] >>> bitIndex) & 1) == 1);
                            }
                            var mask = QRUtil.getMask(maskPattern, row, col - c);
                            if (mask) {
                                dark = !dark;
                            }
                            this.modules[row][col - c] = dark ? 7 : 0;
                            bitIndex--;
                            if (bitIndex == -1) {
                                byteIndex++;
                                bitIndex = 7;
                            }
                        }
                    }
                    row += inc;
                    if (row < 0 || this.moduleCount <= row) {
                        row -= inc;
                        inc = -inc;
                        break;
                    }
                }
            }
        }
    }

    private static class QRMath {
        private static final int[] EXP_TABLE = new int[256];
        private static final int[] LOG_TABLE = new int[256];

        public static int glog(int n) {
            if (n < 1) {
                throw new RuntimeException("glog(" + n + ")");
            }
            return QRMath.LOG_TABLE[n];
        }

        public static int gexp(int n) {
            while (n < 0) {
                n += 255;
            }
            while (n >= 256) {
                n -= 255;
            }
            return QRMath.EXP_TABLE[n];
        }

    }

    private static class QRPolynomial {
        private final int[] num;

        public QRPolynomial(int[] num, int shift) {

            if (num.length == 0) {
                throw new RuntimeException(num.length + "/" + shift);
            }
            var offset = 0;
            while (offset < num.length && num[offset] == 0) {
                offset++;
            }
            this.num = new int[num.length - offset + shift];
            if (num.length - offset >= 0) System.arraycopy(num, offset, this.num, 0, num.length - offset);

        }

        public int get(int index) {
            return this.num[index];
        }

        public int getLength() {
            return this.num.length;
        }

        public QRPolynomial multiply(QRPolynomial e) {
            var num = new int[this.getLength() + e.getLength() - 1];
            for (var i = 0; i < this.getLength(); i++) {
                for (var j = 0; j < e.getLength(); j++) {
                    num[i + j] ^= QRMath.gexp(QRMath.glog(this.get(i)) + QRMath.glog(e.get(j)));
                }
            }
            return new QRPolynomial(num, 0);
        }

        public QRPolynomial mod(QRPolynomial e) {
            if (this.getLength() - e.getLength() < 0) {
                return this;
            }
            var ratio = QRMath.glog(this.get(0)) - QRMath.glog(e.get(0));
            var num = new int[this.getLength()];
            for (var i = 0; i < this.getLength(); i++) {
                num[i] = this.get(i);
            }
            for (var i = 0; i < e.getLength(); i++) {
                num[i] ^= QRMath.gexp(QRMath.glog(e.get(i)) + ratio);
            }
            return new QRPolynomial(num, 0).mod(e);
        }
    }

    private static class QRBitBuffer {
        private int length = 0;
        private int[] buffer = new int[0];

        public void put(int num, int length) {
            for (var i = 0; i < length; i++) {
                this.putBit(((num >>> (length - i - 1)) & 1) == 1);
            }
        }

        public int getLengthInBits() {
            return this.length;
        }

        public void putBit(boolean bit) {
            var bufIndex = Math.floor((double) this.length / 8);
            if (this.buffer.length <= bufIndex) {
                this.buffer = Arrays.copyOf(this.buffer, this.buffer.length + 1);
                this.buffer[this.buffer.length - 1] = 0;
            }
            if (bit) {
                this.buffer[(int) bufIndex] |= (0x80 >>> (this.length % 8));
            }
            this.length++;
        }
    }

    private static class QRUtil {
        private static final int[][] PATTERN_POSITION_TABLE = new int[][]{{}, {6, 18}, {6, 22}, {6, 26}, {6, 30}, {6, 34}, {6, 22, 38}, {6, 24, 42}, {6, 26, 46}, {6, 28, 50}, {6, 30, 54}, {6, 32, 58}, {6, 34, 62}, {6, 26, 46, 66}, {6, 26, 48, 70}, {6, 26, 50, 74}, {6, 30, 54, 78}, {6, 30, 56, 82}, {6, 30, 58, 86}, {6, 34, 62, 90}, {6, 28, 50, 72, 94}, {6, 26, 50, 74, 98}, {6, 30, 54, 78, 102}, {6, 28, 54, 80, 106}, {6, 32, 58, 84, 110}, {6, 30, 58, 86, 114}, {6, 34, 62, 90, 118}, {6, 26, 50, 74, 98, 122}, {6, 30, 54, 78, 102, 126}, {6, 26, 52, 78, 104, 130}, {6, 30, 56, 82, 108, 134}, {6, 34, 60, 86, 112, 138}, {6, 30, 58, 86, 114, 142}, {6, 34, 62, 90, 118, 146}, {6, 30, 54, 78, 102, 126, 150}, {6, 24, 50, 76, 102, 128, 154}, {6, 28, 54, 80, 106, 132, 158}, {6, 32, 58, 84, 110, 136, 162}, {6, 26, 54, 82, 110, 138, 166}, {6, 30, 58, 86, 114, 142, 170}};
        private static final int G15 = (1 << 10) | (1 << 8) | (1 << 5) | (1 << 4) | (1 << 2) | (1 << 1) | (1);
        private static final int G18 = (1 << 12) | (1 << 11) | (1 << 10) | (1 << 9) | (1 << 8) | (1 << 5) | (1 << 2) | (1);
        private static final int G15_MASK = (1 << 14) | (1 << 12) | (1 << 10) | (1 << 4) | (1 << 1);

        public static int getBCHTypeInfo(int data) {
            var d = data << 10;
            while (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G15) >= 0) {
                d ^= (QRUtil.G15 << (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G15)));
            }
            return ((data << 10) | d) ^ QRUtil.G15_MASK;

        }

        public static int getBCHTypeNumber(int data) {
            var d = data << 12;
            while (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G18) >= 0) {
                d ^= (QRUtil.G18 << (QRUtil.getBCHDigit(d) - QRUtil.getBCHDigit(QRUtil.G18)));
            }
            return (data << 12) | d;

        }

        public static int getBCHDigit(int data) {
            var digit = 0;
            while (data != 0) {
                digit++;
                data >>>= 1;
            }
            return digit;
        }

        public static int[] getPatternPosition(int typeNumber) {
            return QRUtil.PATTERN_POSITION_TABLE[typeNumber - 1];
        }

        public static boolean getMask(int maskPattern, int b, int c) {
            final int i = b * c % 2 + b * c % 3;
            return switch (maskPattern) {
                case QRMaskPattern.PATTERN000 -> 0 == (b + c) % 2;
                case QRMaskPattern.PATTERN001 -> 0 == b % 2;
                case QRMaskPattern.PATTERN010 -> 0 == c % 3;
                case QRMaskPattern.PATTERN011 -> 0 == (b + c) % 3;
                case QRMaskPattern.PATTERN100 -> 0 == (Math.floor((double) b / 2) + Math.floor((double) c / 3)) % 2;
                case QRMaskPattern.PATTERN101 -> 0 == i;
                case QRMaskPattern.PATTERN110 -> 0 == (i) % 2;
                case QRMaskPattern.PATTERN111 -> 0 == (b * c % 3 + (b + c) % 2) % 2;
                default -> throw new RuntimeException("bad maskPattern:" + maskPattern);

            };
        }

        public static QRPolynomial getErrorCorrectPolynomial(int errorCorrectLength) {
            var a = new QRPolynomial(new int[]{1}, 0);
            for (var i = 0; i < errorCorrectLength; i++) {
                a = a.multiply(new QRPolynomial(new int[]{1, QRMath.gexp(i)}, 0));
            }
            return a;

        }

        public static int getLengthInBits(int mode, int type) {
            if (1 <= type && type < 10) {
                return switch (mode) {
                    case QRMode.MODE_NUMBER -> 10;
                    case QRMode.MODE_ALPHA_NUM -> 9;
                    case QRMode.MODE_8BIT_BYTE, QRMode.MODE_KANJI -> 8;
                    default -> throw new RuntimeException("mode:" + mode);
                };
            } else if (type < 27) {
                return switch (mode) {
                    case QRMode.MODE_NUMBER -> 12;
                    case QRMode.MODE_ALPHA_NUM -> 11;
                    case QRMode.MODE_8BIT_BYTE -> 16;
                    case QRMode.MODE_KANJI -> 10;
                    default -> throw new RuntimeException("mode:" + mode);
                };

            } else if (type < 41) {
                return switch (mode) {
                    case QRMode.MODE_NUMBER -> 14;
                    case QRMode.MODE_ALPHA_NUM -> 13;
                    case QRMode.MODE_8BIT_BYTE -> 16;
                    case QRMode.MODE_KANJI -> 12;
                    default -> throw new RuntimeException("mode:" + mode);
                };
            } else {
                throw new RuntimeException("type:" + type);
            }
        }

        public static int getLostPoint(QRCodeModel qrCode) {
            var moduleCount = qrCode.getModuleCount();
            var lostPoint = 0;
            for (var row = 0; row < moduleCount; row++) {
                for (var col = 0; col < moduleCount; col++) {
                    var sameCount = 0;
                    var dark = qrCode.isDark(row, col);
                    for (var r = -1; r <= 1; r++) {
                        if (row + r < 0 || moduleCount <= row + r) {
                            continue;
                        }
                        for (var c = -1; c <= 1; c++) {
                            if (col + c < 0 || moduleCount <= col + c) {
                                continue;
                            }
                            if (r == 0 && c == 0) {
                                continue;
                            }
                            if (dark == qrCode.isDark(row + r, col + c)) {
                                sameCount++;
                            }
                        }
                    }
                    if (sameCount > 5) {
                        lostPoint += (3 + sameCount - 5);
                    }
                }
            }
            for (var row = 0; row < moduleCount - 1; row++) {
                for (var col = 0; col < moduleCount - 1; col++) {
                    var count = 0;
                    if (qrCode.isDark(row, col) == 1) count++;
                    if (qrCode.isDark(row + 1, col) == 1) count++;
                    if (qrCode.isDark(row, col + 1) == 1) count++;
                    if (qrCode.isDark(row + 1, col + 1) == 1) count++;
                    if (count == 0 || count == 4) {
                        lostPoint += 3;
                    }
                }
            }
            for (var row = 0; row < moduleCount; row++) {
                for (var col = 0; col < moduleCount - 6; col++) {
                    if (qrCode.isDark(row, col) == 1 && qrCode.isDark(row, col + 1) == 0 && qrCode.isDark(row, col + 2) == 1 && qrCode.isDark(row, col + 3) == 1 && qrCode.isDark(row, col + 4) == 1 && qrCode.isDark(row, col + 5) == 0 && qrCode.isDark(row, col + 6) == 1) {
                        lostPoint += 40;
                    }
                }
            }
            for (var col = 0; col < moduleCount; col++) {
                for (var row = 0; row < moduleCount - 6; row++) {
                    if (qrCode.isDark(row, col) == 1 && qrCode.isDark(row + 1, col) == 0 && qrCode.isDark(row + 2, col) == 1 && qrCode.isDark(row + 3, col) == 1 && qrCode.isDark(row + 4, col) == 1 && qrCode.isDark(row + 5, col) == 0 && qrCode.isDark(row + 6, col) == 1) {
                        lostPoint += 40;
                    }
                }
            }
            var darkCount = 0;
            for (var col = 0; col < moduleCount; col++) {
                for (var row = 0; row < moduleCount; row++) {
                    if (qrCode.isDark(row, col) == 1) {
                        darkCount++;
                    }
                }
            }
            var ratio = Math.abs(100 * darkCount / moduleCount / moduleCount - 50) / 5;
            lostPoint += ratio * 10;
            return lostPoint;
        }

    }

    private static class QRRSBlock {

        private final int totalCount;
        private final int dataCount;
        private static final int[][] RS_BLOCK_TABLE = {{1, 26, 19}, {1, 26, 16}, {1, 26, 13}, {1, 26, 9}, {1, 44, 34}, {1, 44, 28}, {1, 44, 22}, {1, 44, 16}, {1, 70, 55}, {1, 70, 44}, {2, 35, 17}, {2, 35, 13}, {1, 100, 80}, {2, 50, 32}, {2, 50, 24}, {4, 25, 9}, {1, 134, 108}, {2, 67, 43}, {2, 33, 15, 2, 34, 16}, {2, 33, 11, 2, 34, 12}, {2, 86, 68}, {4, 43, 27}, {4, 43, 19}, {4, 43, 15}, {2, 98, 78}, {4, 49, 31}, {2, 32, 14, 4, 33, 15}, {4, 39, 13, 1, 40, 14}, {2, 121, 97}, {2, 60, 38, 2, 61, 39}, {4, 40, 18, 2, 41, 19}, {4, 40, 14, 2, 41, 15}, {2, 146, 116}, {3, 58, 36, 2, 59, 37}, {4, 36, 16, 4, 37, 17}, {4, 36, 12, 4, 37, 13}, {2, 86, 68, 2, 87, 69}, {4, 69, 43, 1, 70, 44}, {6, 43, 19, 2, 44, 20}, {6, 43, 15, 2, 44, 16}, {4, 101, 81}, {1, 80, 50, 4, 81, 51}, {4, 50, 22, 4, 51, 23}, {3, 36, 12, 8, 37, 13}, {2, 116, 92, 2, 117, 93}, {6, 58, 36, 2, 59, 37}, {4, 46, 20, 6, 47, 21}, {7, 42, 14, 4, 43, 15}, {4, 133, 107}, {8, 59, 37, 1, 60, 38}, {8, 44, 20, 4, 45, 21}, {12, 33, 11, 4, 34, 12}, {3, 145, 115, 1, 146, 116}, {4, 64, 40, 5, 65, 41}, {11, 36, 16, 5, 37, 17}, {11, 36, 12, 5, 37, 13}, {5, 109, 87, 1, 110, 88}, {5, 65, 41, 5, 66, 42}, {5, 54, 24, 7, 55, 25}, {11, 36, 12}, {5, 122, 98, 1, 123, 99}, {7, 73, 45, 3, 74, 46}, {15, 43, 19, 2, 44, 20}, {3, 45, 15, 13, 46, 16}, {1, 135, 107, 5, 136, 108}, {10, 74, 46, 1, 75, 47}, {1, 50, 22, 15, 51, 23}, {2, 42, 14, 17, 43, 15}, {5, 150, 120, 1, 151, 121}, {9, 69, 43, 4, 70, 44}, {17, 50, 22, 1, 51, 23}, {2, 42, 14, 19, 43, 15}, {3, 141, 113, 4, 142, 114}, {3, 70, 44, 11, 71, 45}, {17, 47, 21, 4, 48, 22}, {9, 39, 13, 16, 40, 14}, {3, 135, 107, 5, 136, 108}, {3, 67, 41, 13, 68, 42}, {15, 54, 24, 5, 55, 25}, {15, 43, 15, 10, 44, 16}, {4, 144, 116, 4, 145, 117}, {17, 68, 42}, {17, 50, 22, 6, 51, 23}, {19, 46, 16, 6, 47, 17}, {2, 139, 111, 7, 140, 112}, {17, 74, 46}, {7, 54, 24, 16, 55, 25}, {34, 37, 13}, {4, 151, 121, 5, 152, 122}, {4, 75, 47, 14, 76, 48}, {11, 54, 24, 14, 55, 25}, {16, 45, 15, 14, 46, 16}, {6, 147, 117, 4, 148, 118}, {6, 73, 45, 14, 74, 46}, {11, 54, 24, 16, 55, 25}, {30, 46, 16, 2, 47, 17}, {8, 132, 106, 4, 133, 107}, {8, 75, 47, 13, 76, 48}, {7, 54, 24, 22, 55, 25}, {22, 45, 15, 13, 46, 16}, {10, 142, 114, 2, 143, 115}, {19, 74, 46, 4, 75, 47}, {28, 50, 22, 6, 51, 23}, {33, 46, 16, 4, 47, 17}, {8, 152, 122, 4, 153, 123}, {22, 73, 45, 3, 74, 46}, {8, 53, 23, 26, 54, 24}, {12, 45, 15, 28, 46, 16}, {3, 147, 117, 10, 148, 118}, {3, 73, 45, 23, 74, 46}, {4, 54, 24, 31, 55, 25}, {11, 45, 15, 31, 46, 16}, {7, 146, 116, 7, 147, 117}, {21, 73, 45, 7, 74, 46}, {1, 53, 23, 37, 54, 24}, {19, 45, 15, 26, 46, 16}, {5, 145, 115, 10, 146, 116}, {19, 75, 47, 10, 76, 48}, {15, 54, 24, 25, 55, 25}, {23, 45, 15, 25, 46, 16}, {13, 145, 115, 3, 146, 116}, {2, 74, 46, 29, 75, 47}, {42, 54, 24, 1, 55, 25}, {23, 45, 15, 28, 46, 16}, {17, 145, 115}, {10, 74, 46, 23, 75, 47}, {10, 54, 24, 35, 55, 25}, {19, 45, 15, 35, 46, 16}, {17, 145, 115, 1, 146, 116}, {14, 74, 46, 21, 75, 47}, {29, 54, 24, 19, 55, 25}, {11, 45, 15, 46, 46, 16}, {13, 145, 115, 6, 146, 116}, {14, 74, 46, 23, 75, 47}, {44, 54, 24, 7, 55, 25}, {59, 46, 16, 1, 47, 17}, {12, 151, 121, 7, 152, 122}, {12, 75, 47, 26, 76, 48}, {39, 54, 24, 14, 55, 25}, {22, 45, 15, 41, 46, 16}, {6, 151, 121, 14, 152, 122}, {6, 75, 47, 34, 76, 48}, {46, 54, 24, 10, 55, 25}, {2, 45, 15, 64, 46, 16}, {17, 152, 122, 4, 153, 123}, {29, 74, 46, 14, 75, 47}, {49, 54, 24, 10, 55, 25}, {24, 45, 15, 46, 46, 16}, {4, 152, 122, 18, 153, 123}, {13, 74, 46, 32, 75, 47}, {48, 54, 24, 14, 55, 25}, {42, 45, 15, 32, 46, 16}, {20, 147, 117, 4, 148, 118}, {40, 75, 47, 7, 76, 48}, {43, 54, 24, 22, 55, 25}, {10, 45, 15, 67, 46, 16}, {19, 148, 118, 6, 149, 119}, {18, 75, 47, 31, 76, 48}, {34, 54, 24, 34, 55, 25}, {20, 45, 15, 61, 46, 16}};

        public QRRSBlock(int a, int b) {
            this.totalCount = a;
            this.dataCount = b;
        }

        public static List<QRRSBlock> getRSBlocks(int typeNumber, int errorCorrectLevel) {
            var rsBlock = QRRSBlock.getRsBlockTable(typeNumber, errorCorrectLevel);

            var length = rsBlock.length / 3;
            List<QRRSBlock> list = new ArrayList<>();
            for (var i = 0; i < length; i++) {
                var count = rsBlock[i * 3];
                var totalCount = rsBlock[i * 3 + 1];
                var dataCount = rsBlock[i * 3 + 2];
                for (var j = 0; j < count; j++) {
                    list.add(new QRRSBlock(totalCount, dataCount));
                }
            }
            return list;
        }

        public static int[] getRsBlockTable(int typeNumber, int errorCorrectLevel) {
            return switch (errorCorrectLevel) {
                case QRErrorCorrectLevel.L -> RS_BLOCK_TABLE[4 * (typeNumber - 1)];
                case QRErrorCorrectLevel.M -> RS_BLOCK_TABLE[4 * (typeNumber - 1) + 1];
                case QRErrorCorrectLevel.Q -> RS_BLOCK_TABLE[4 * (typeNumber - 1) + 2];
                case QRErrorCorrectLevel.H -> RS_BLOCK_TABLE[4 * (typeNumber - 1) + 3];
                default ->
                        throw new RuntimeException("bad rs block @ typeNumber:" + typeNumber + "/errorCorrectLevel:" + errorCorrectLevel);
            };
        }
    }
}
