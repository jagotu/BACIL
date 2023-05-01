package com.vztekoverflow.cilostazol.runtime.typesystem.method;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.CILParserException;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.signature.LocalVarsSig;
import com.vztekoverflow.cil.parser.cli.signature.MethodDefSig;
import com.vztekoverflow.cil.parser.cli.signature.SignatureReader;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cil.parser.cli.table.generated.CLIMethodDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;

public class MethodFactory {
    //region Flags
    //region Method headers
    private static final byte CORILMETHOD_TINYFORMAT = 2;
    private static final byte CORILMETHOD_FATFORMAT = 3;
    private static final byte CORILMETHOD_INITLOCALS = 0x10;
    private static final byte CORILMETHOD_MORESECTS = 0x8;
    //endregion
    //region Data section
    public static final byte CORILMETHOD_SECT_FATFORMAT = 0x40;
    public static final byte CORILMETHOD_SECT_EHTABLE = 0x1;
    public static final byte CORILMETHOD_SECT_OPTILTABLE = 0x2;
    public static final int CORILMETHOD_SECT_MORESECTS = 0x80;
    //endregion
    //endregion

    public static Method createMethod(CLIMethodDefTableRow methodDef, CLIFile file) {
        String methodName = methodDef.getName().read(file.getStringHeap());
        MethodDefSig signature = MethodDefSig.parse(new SignatureReader(methodDef.getSignature().read(file.getBlobHeap())), file);

        ByteSequenceBuffer buf = file.getBuffer(methodDef.getRVA());
        byte firstByte = buf.getByte();

        final int size;
        final int ILflags;
        final short maxStack;
        final boolean initLocals;
        final LocalVarsSig vars;
        final int argsCount;
        final int varsCount;


        if((firstByte & 3) == CORILMETHOD_TINYFORMAT)
        {
            //II.25.4.2 Tiny format
            ILflags = CORILMETHOD_TINYFORMAT;
            maxStack = 8;
            size = (firstByte >> 2) & 0xFF;
            initLocals = false;
            vars = null;

        } else if((firstByte & 3) == CORILMETHOD_FATFORMAT) {
            short firstWord = (short)(firstByte | (buf.getByte() << 8));
            ILflags = firstWord & 0xFFF;
            byte headerSize = (byte)(firstWord >> 12);
            if(headerSize != 3)
            {
                throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.fatHeader.size"));
            }
            maxStack = buf.getShort();
            size = buf.getInt();
            int localVarSigTok = buf.getInt();
            if(localVarSigTok == 0)
            {
                vars = null;
            } else {
                CLITablePtr localVarSigPtr = CLITablePtr.fromToken(localVarSigTok);
                byte[] localVarSig = file.getTableHeads().getStandAloneSigTableHead().skip(localVarSigPtr).getSignature().read(file.getBlobHeap());
                vars = LocalVarsSig.read(new SignatureReader(localVarSig), file);
            }

            initLocals = (ILflags & CORILMETHOD_INITLOCALS) != 0;
        } else {
            throw new CILParserException(CILOSTAZOLBundle.message("cilostazol.exception.parser.CorILMethod.flags.invalid"));
        }

        final byte[] body = buf.subSequence(size).toByteArray();

        buf.setPosition(buf.getPosition() + size - 1);
            if((ILflags & CORILMETHOD_MORESECTS) != 0)
            {
                int a1 = buf.getByte();
                int a2 = buf.getByte();
                int a3 = buf.getByte();
                int a4 = buf.getByte();
                int a5 = buf.getByte();
                int a6 = buf.getByte();
                int a7 = buf.getByte();
                int a8 = buf.getByte();
                int b1 = buf.getByte();
                int b2 = buf.getByte();
                int b3 = buf.getByte();
                int b4 = buf.getByte();
                int b5 = buf.getByte();
                int b6 = buf.getByte();
                int b7 = buf.getByte();
                int b8 = buf.getByte();
                int c1 = buf.getByte();
                int c2 = buf.getByte();
                int c3 = buf.getByte();
                int c4 = buf.getByte();
                int c5 = buf.getByte();
                int c6 = buf.getByte();
                int c7 = buf.getByte();
                int c8 = buf.getByte();
            System.out.println(firstByte);
        }


        //TODO: Create node

        int explicitArgsStart = 0;
        if ( signature.isHasThis() && !signature.isExplicitThis()) {
            // TODO Check defining type
            explicitArgsStart = 1;
        }
        argsCount = signature.getParamsCount() + explicitArgsStart;

        if(vars != null)
        {
            varsCount = vars.getVarsCount();
        } else {
            varsCount = 0;
        }

        //TODO: Tomas -> create the method.
        return null;
    }
}
