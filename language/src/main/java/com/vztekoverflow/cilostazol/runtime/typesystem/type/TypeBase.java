package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITableConstants;
import com.vztekoverflow.cil.parser.cli.table.generated.CLITypeDefTableRow;
import com.vztekoverflow.cilostazol.CILOSTAZOLBundle;
import com.vztekoverflow.cilostazol.runtime.typesystem.TypeSystemException;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.IField;
import com.vztekoverflow.cilostazol.runtime.typesystem.field.factory.FieldFactory;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.IMethod;
import com.vztekoverflow.cilostazol.runtime.typesystem.method.factory.MethodFactory;

import java.util.ArrayList;

public abstract class TypeBase<T extends CLITableRow<T>> implements IType {
    protected final CLIFile _definingFile;
    protected final String _name;
    protected final String _namespace;
    protected final IType _directBaseClass;
    protected final IType[] _interfaces;
    protected IMethod[] _methods;
    protected IMethod[] _vMethodTable;
    protected IField[] _fields;
    protected final IComponent _definingComponent;

    protected final T _row;

    public TypeBase(T row,
                    CLIFile _definingFile,
                    String _name,
                    String _namespace,
                    IType _directBaseClass,
                    IType[] _interfaces,
                    IComponent _definingComponent) {
        _row = row;
        this._definingFile = _definingFile;
        this._name = _name;
        this._namespace = _namespace;
        this._directBaseClass = _directBaseClass;
        this._interfaces = _interfaces;
        this._definingComponent = _definingComponent;
        this._fields = null;
        this._methods = null;
        this._vMethodTable = null;
    }

    //region IType
    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getNamespace() {
        return _namespace;
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

    private IField[] createFields(CLITypeDefTableRow row) {
        var fieldTablePtr = row.getFieldListTablePtr();
        var fieldListEndPtr = row.skip(1).getFieldListTablePtr();

        var fieldRow = _definingComponent.getTableHeads().getFieldTableHead().skip(fieldTablePtr);

        var fields = new ArrayList<IField>();
        while (fieldRow.getRowNo() < fieldListEndPtr.getRowNo() && fieldRow.hasNext()) {
            var field = FieldFactory.create(fieldRow, _definingComponent);
            fields.add(field);
            fieldRow = fieldRow.next();
        }

        return fields.toArray(new IField[0]);
    }


    private IMethod[] createMethods(CLITypeDefTableRow row) {
        var methodTablePtr = row.getMethodListTablePtr();
        var methodListEndPtr = row.skip(1).getMethodListTablePtr();

        var methodRow = _definingComponent.getTableHeads().getMethodDefTableHead().skip(methodTablePtr);

        var methods = new ArrayList<IMethod>();
        while (methodRow.getRowNo() < methodListEndPtr.getRowNo() && methodRow.hasNext()) {
            var method = MethodFactory.create(methodRow, this);
            methods.add(method);
            methodRow = methodRow.next();
        }

        return methods.toArray(new IMethod[0]);
    }

    @Override
    public IComponent getDefiningComponent() {
        return _definingComponent;
    }

    @Override
    public IType getDefinition() {
        return this;
    }
    //endregion
}
