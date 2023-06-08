package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.staticobject.StaticShape;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.Field;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.factory.FieldFactory;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.factory.MethodFactory;

import java.util.ArrayList;

public abstract class TypeBase<T extends CLITableRow<T>> extends CLIType implements IType {

    private final static int ABSTRACT_FLAG_MASK = 0x80;
    private final static int SEALED_FLAG_MASK = 0x100;
    private final static int SPECIAL_NAME_FLAG_MASK = 0x400;
    private final static int IMPORT_FLAG_MASK = 0x1000;
    private final static int SERIALIZABLE_FLAG_MASK = 0x2000;
    private final static int BEFORE_FIELD_INIT_FLAG_MASK = 0x100000;
    private final static int RT_SPECIAL_NAME_FLAG_MASK = 0x800;
    private final static int HAS_SECURITY_FLAG_MASK = 0x40000;
    private final static int IS_TYPE_FORWARDER_FLAG_MASK = 0x200000;

    @CompilerDirectives.CompilationFinal
    private StaticShape<StaticObject.StaticObjectFactory> instanceShape;
    @CompilerDirectives.CompilationFinal
    private StaticShape<StaticObject.StaticObjectFactory> staticShape;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private Field[] instanceFields;
    @CompilerDirectives.CompilationFinal(dimensions = 1)
    private Field[] staticFields;
    @CompilerDirectives.CompilationFinal
    private TypeBase<?> superClass;

    protected final int _flags;
    protected final CLIFile _definingFile;
    protected final String _name;
    protected final String _namespace;
    protected final IType _directBaseClass;
    protected final IType[] _interfaces;
    protected IMethod[] _methods;
    protected IMethod[] _vMethodTable;
    protected IField[] _fields;
    protected final CLIComponent _definingComponent;
    protected final T _row;

    public TypeBase(CILOSTAZOLContext context, T row, CLIFile _definingFile, String _name, String _namespace, IType _directBaseClass, IType[] _interfaces, CLIComponent _definingComponent, int flags) {
        super(context);
        _row = row;
        this._definingFile = _definingFile;
        this._name = _name;
        this._namespace = _namespace;
        this._directBaseClass = _directBaseClass;
        this._interfaces = _interfaces;
        this._definingComponent = _definingComponent;
        this._flags = flags;
        this._fields = null;
        this._methods = null;
        this._vMethodTable = null;

        // TODO
        this.superClass = null;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getNamespace() {
        return _namespace;
    }

    @Override
    public CLIComponent getCLIComponent() {
        return _definingComponent;
    }

    @Override
    public CLIFile getDefiningFile() {
        return _definingFile;
    }

    @Override
    public IType getDirectBaseClass() {

        return _directBaseClass;
    }

    @Override
    public IType[] getInterfaces() {
        return _interfaces;
    }

    @Override
    public IMethod[] getMethods() {
        if (_methods == null) {
            //TODO: create on demand
            _methods = switch (_row.getTableId()) {
                case CLITableConstants.CLI_TABLE_TYPE_DEF -> createMethods((CLITypeDefTableRow) _row);
                case CLITableConstants.CLI_TABLE_TYPE_REF -> null; //TODO: implement case for ref table
                case CLITableConstants.CLI_TABLE_TYPE_SPEC -> null; //TODO: implement case for spec table
                default -> throw new TypeSystemException(CILOSTAZOLBundle.message("typeSystem.unknownTableType"));
            };
        }

        return _methods;
    }

    @Override
    public IMethod[] getVTable() {
        if (_vMethodTable == null) {
            //TODO: create on demand
            _vMethodTable = switch (_row.getTableId()) {
                case CLITableConstants.CLI_TABLE_TYPE_DEF -> null; //TODO: implement case for def table
                case CLITableConstants.CLI_TABLE_TYPE_REF -> null; //TODO: implement case for ref table
                case CLITableConstants.CLI_TABLE_TYPE_SPEC -> null; //TODO: implement case for spec table
                default -> throw new TypeSystemException(CILOSTAZOLBundle.message("typeSystem.unknownTableType"));
            };
        }

        return _vMethodTable;
    }

    @Override
    public IField[] getFields() {
        if (_fields == null) {
            _fields = switch (_row.getTableId()) {
                case CLITableConstants.CLI_TABLE_TYPE_DEF -> createFields((CLITypeDefTableRow) _row);
                case CLITableConstants.CLI_TABLE_TYPE_REF -> null; //TODO: implement case for ref table
                case CLITableConstants.CLI_TABLE_TYPE_SPEC -> null; //TODO: implement case for spec table
                default -> throw new TypeSystemException(CILOSTAZOLBundle.message("typeSystem.unknownTableType"));
            };
        }
        return _fields;
    }

    @Override
    public IComponent getDefiningComponent() {
        return _definingComponent;
    }

    @Override
    public IType getDefinition() {
        return this;
    }

    public TypeVisibility getVisibility() {
        return TypeVisibility.fromFlags(_flags & TypeVisibility.MASK);
    }

    public TypeLayout getLayout() {
        return TypeLayout.fromFlags(_flags & TypeLayout.MASK);
    }

    public TypeSemantics getSemantics() {
        return TypeSemantics.fromFlags(_flags & TypeSemantics.MASK);
    }

    public boolean isClass() {
        return !isInterface();
    }

    @Override
    public boolean isInterface() {
        return getSemantics() == TypeSemantics.Interface;
    }

    @Override
    public boolean isAbstract() {
        return (_flags & ABSTRACT_FLAG_MASK) != 0;
    }

    // TODO
    @Override
    protected int getHierarchyDepth() {
        return 0;
    }

    // TODO
    @Override
    protected CLIType[] getSuperTypes() {
        return new CLIType[0];
    }

    public boolean isSealed() {
        return (_flags & SEALED_FLAG_MASK) != 0;
    }

    public boolean isSpecialName() {
        return (_flags & SPECIAL_NAME_FLAG_MASK) != 0;
    }

    public boolean isImport() {
        return (_flags & IMPORT_FLAG_MASK) != 0;
    }

    public boolean isSerializable() {
        return (_flags & SERIALIZABLE_FLAG_MASK) != 0;
    }

    public boolean isBeforeFieldInit() {
        return (_flags & BEFORE_FIELD_INIT_FLAG_MASK) != 0;
    }

    public boolean isRTSpecialName() {
        return (_flags & RT_SPECIAL_NAME_FLAG_MASK) != 0;
    }

    public boolean hasSecurity() {
        return (_flags & HAS_SECURITY_FLAG_MASK) != 0;
    }

    public boolean isTypeForwarder() {
        return (_flags & IS_TYPE_FORWARDER_FLAG_MASK) != 0;
    }

    public StaticShape<StaticObject.StaticObjectFactory> getShape(boolean isStatic) {
        if (isStatic && staticShape == null || !isStatic && instanceShape == null) {
            createShapes();
        }

        return isStatic ? staticShape : instanceShape;
    }

    public Field[] getInstanceFields() {
        if (instanceShape == null) {
            createShapes();
        }

        return instanceFields;
    }

    public Field[] getStaticFields() {
        if (staticFields == null) {
            createShapes();
        }

        return staticFields;
    }

    // TODO
    public void safelyInitialize() {
    }

    private IField[] createFields(CLITypeDefTableRow row) {
        var fieldTablePtr = row.getFieldListTablePtr();
        var fieldListEndPtr = row.skip(1).getFieldListTablePtr();

        var fieldRow = _definingComponent.getTableHeads().getFieldTableHead().skip(fieldTablePtr);

        var fields = new ArrayList<IField>();
        while (fieldRow.getRowNo() < fieldListEndPtr.getRowNo() && fieldRow.hasNext()) {
            var field = FieldFactory.create(getContext(), fieldRow, new IType[0], (this instanceof NonGenericType) ? new IType[0] : getTypeParameters(), _definingComponent);
            fields.add(field);
            fieldRow = fieldRow.next();
        }

        return fields.toArray(new IField[0]);
    }

    private IMethod[] createMethods(CLITypeDefTableRow row) {
        var methodTablePtr = row.getMethodListTablePtr();

        final boolean isLastType = row.getRowNo() == getDefiningFile().getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_TYPE_DEF);
        final int lastIdx = isLastType ? getDefiningFile().getTablesHeader().getRowCount(CLITableConstants.CLI_TABLE_METHOD_DEF) : row.skip(1).getMethodListTablePtr().getRowNo();

        var methodRow = _definingComponent.getTableHeads().getMethodDefTableHead().skip(methodTablePtr);

        var methods = new ArrayList<IMethod>();
        while (methodRow.getRowNo() < lastIdx) {
            var method = MethodFactory.create(getContext(), methodRow, this);
            methods.add(method);
            methodRow = methodRow.next();
        }

        return methods.toArray(new IMethod[0]);
    }

    private void createShapes() {
        // TODO: Is this invalidation necessary when initializing CompilationFinal fields?
        CompilerDirectives.transferToInterpreterAndInvalidate();

        LinkedFieldLayout layout = new LinkedFieldLayout(getContext(), this, superClass);
        instanceShape = layout.instanceShape;
        staticShape = layout.staticShape;
        instanceFields = layout.instanceFields;
        staticFields = layout.staticFields;
    }
}
