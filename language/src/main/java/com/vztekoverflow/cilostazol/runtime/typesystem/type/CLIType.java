package com.vztekoverflow.cilostazol.runtime.typesystem.type;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.staticobject.StaticShape;
import com.vztekoverflow.cil.parser.cli.CLIFile;
import com.vztekoverflow.cilostazol.meta.SystemTypes;
import com.vztekoverflow.cilostazol.objectmodel.LinkedField;
import com.vztekoverflow.cilostazol.objectmodel.StaticObject;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.CLIComponent;
import com.vztekoverflow.cilostazol.runtime.typesystem.component.IComponent;

public abstract class CLIType implements IType {
    private final StaticShape<StaticObject.StaticObjectFactory> instanceShape;

    private final StaticShape<StaticObject.StaticObjectFactory> staticShape;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    final LinkedField[] instanceFields;

    @CompilerDirectives.CompilationFinal(dimensions = 1)
    final LinkedField[] staticFields;

    private SystemTypes kind;

    public abstract CLIComponent getCLIComponent();

    public CLIFile getDefiningFile() {
        return getCLIComponent().getDefiningFile();
    }

    @Override
    public IComponent getDefiningComponent() {
        return getCLIComponent();
    }

    @Override
    public SystemTypes getKind() {
        return kind;
    }

    public StaticShape<StaticObject.StaticObjectFactory> getShape(boolean isStatic) {
        return isStatic ? staticShape : instanceShape;
    }

    public LinkedField[] getInstanceFields() {
        return instanceFields;
    }

    public LinkedField[] getStaticFields() {
        return staticFields;
    }
}
