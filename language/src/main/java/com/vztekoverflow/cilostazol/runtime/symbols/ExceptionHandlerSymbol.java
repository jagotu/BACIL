package com.vztekoverflow.cilostazol.runtime.symbols;

import com.vztekoverflow.cil.parser.ByteSequenceBuffer;
import com.vztekoverflow.cil.parser.cli.table.CLITablePtr;
import com.vztekoverflow.cilostazol.runtime.other.ContextProviderImpl;

public final class ExceptionHandlerSymbol extends Symbol {
  private final int tryOffset;
  private final int tryLength;

  public int getTryOffset() {
    return tryOffset;
  }

  public int getTryLength() {
    return tryLength;
  }

  public int getHandlerOffset() {
    return handlerOffset;
  }

  public int getHandlerLength() {
    return handlerLength;
  }

  public TypeSymbol getHandlerException() {
    return handlerException;
  }

  public ExceptionClauseFlags getFlags() {
    return flags;
  }

  private final int handlerOffset;
  private final int handlerLength;
  private final TypeSymbol handlerException;
  private final ExceptionClauseFlags flags;

  private ExceptionHandlerSymbol(
      int tryOffset,
      int tryLength,
      int handlerOffset,
      int handlerLength,
      TypeSymbol handlerException,
      ExceptionClauseFlags flags) {
    super(ContextProviderImpl.getInstance());
    this.tryOffset = tryOffset;
    this.tryLength = tryLength;
    this.handlerOffset = handlerOffset;
    this.handlerLength = handlerLength;
    this.handlerException = handlerException;
    this.flags = flags;
  }

  public static final class ExceptionHandlerSymbolFactory {
    public static ExceptionHandlerSymbol[] create(
        ByteSequenceBuffer buf, TypeSymbol[] mvars, TypeSymbol[] vars, ModuleSymbol module) {
      final ExceptionHandlerSymbol[] handlers;
      final byte kind = buf.getByte();

      final MethodSectionFlags sectionFlags = new MethodSectionFlags(kind);
      final int dataSize;
      final int clauses;

      if (!sectionFlags.hasFlag(MethodSectionFlags.Flag.CORILMETHOD_SECT_EHTABLE))
        return new ExceptionHandlerSymbol[0];

      if (sectionFlags.hasFlag(MethodSectionFlags.Flag.CORILMETHOD_SECT_FATFORMAT)) {
        dataSize = buf.getByte() | buf.getByte() << 8 | buf.getByte() << 16;
        clauses = (dataSize - 4) / 12;
        handlers = new ExceptionHandlerSymbol[clauses];
      } else {
        dataSize = buf.getByte();
        buf.getShort(); // Reserved
        clauses = (dataSize - 4) / 12;
        handlers = new ExceptionHandlerSymbol[clauses];
      }

      for (int i = 0; i < clauses; i++) {
        final ExceptionClauseFlags flags;
        final int tryoffset, trylength, handleroffset, handlerlength, classTokenOrFilterOffset;
        if (sectionFlags.hasFlag(MethodSectionFlags.Flag.CORILMETHOD_SECT_FATFORMAT)) {
          flags = new ExceptionClauseFlags(buf.getInt());
          tryoffset = buf.getInt();
          trylength = buf.getInt();
          handleroffset = buf.getInt();
          handlerlength = buf.getInt();
          classTokenOrFilterOffset = buf.getInt();
        } else {
          flags = new ExceptionClauseFlags(buf.getShort());
          tryoffset = buf.getShort();
          trylength = buf.getByte();
          handleroffset = buf.getShort();
          handlerlength = buf.getByte();
          classTokenOrFilterOffset = buf.getInt();
        }
        final TypeSymbol klass =
            (flags.hasFlag(ExceptionClauseFlags.Flag.COR_ILEXCEPTION_CLAUSE_EXCEPTION))
                ? NamedTypeSymbol.NamedTypeSymbolFactory.create(
                    CLITablePtr.fromToken(classTokenOrFilterOffset), mvars, vars, module)
                : null;
        handlers[i] =
            new ExceptionHandlerSymbol(
                tryoffset, trylength, handleroffset, handlerlength, klass, flags);
      }
      return handlers;
    }

    public static ExceptionHandlerSymbol createWith(
        ExceptionHandlerSymbol symbol, TypeSymbol exception) {
      return new ExceptionHandlerSymbol(
          symbol.getTryOffset(),
          symbol.getTryLength(),
          symbol.getHandlerOffset(),
          symbol.getHandlerLength(),
          exception,
          symbol.getFlags());
    }
  }

  public static class MethodSectionFlags {
    public final int _flags;

    public MethodSectionFlags(int flags) {
      _flags = flags;
    }

    public boolean hasFlag(MethodSectionFlags.Flag flag) {
      return (_flags & flag.code) == flag.code;
    }

    public enum Flag {
      CORILMETHOD_SECT_EHTABLE(0x1),
      CORILMETHOD_SECT_OPTILTABLE(0x2),
      CORILMETHOD_SECT_FATFORMAT(0x40),
      CORILMETHOD_SECT_MORESECTS(0x80);

      public final int code;

      Flag(int code) {
        this.code = code;
      }
    }
  }

  public static class ExceptionClauseFlags {
    public final int _flags;

    public ExceptionClauseFlags(int flags) {
      _flags = flags;
    }

    public boolean hasFlag(ExceptionClauseFlags.Flag flag) {
      return _flags == flag.code;
    }

    public enum Flag {
      COR_ILEXCEPTION_CLAUSE_EXCEPTION(0x0),
      COR_ILEXCEPTION_CLAUSE_FILTER(0x1),
      COR_ILEXCEPTION_CLAUSE_FINALLY(0x2),
      COR_ILEXCEPTION_CLAUSE_FAULT(0x4);

      public final int code;

      Flag(int code) {
        this.code = code;
      }
    }
  }
}
