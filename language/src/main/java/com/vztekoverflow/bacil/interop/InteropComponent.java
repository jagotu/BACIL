package com.vztekoverflow.bacil.interop;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.vztekoverflow.bacil.runtime.bacil.BACILComponent;

@ExportLibrary(InteropLibrary.class)
public class InteropComponent implements TruffleObject {

    private final BACILComponent component;

    public InteropComponent(BACILComponent component) {
        this.component = component;
    }

    private String getNamespace(String member)
    {
        int lastDot = member.lastIndexOf('.');
        if (lastDot == -1)
        {
            return "";
        } else {
            return member.substring(0, lastDot);
        }
    }


    private String getName(String member)
    {
        int lastDot = member.lastIndexOf('.');
        if (lastDot == -1)
        {
            return member;
        } else {
            return member.substring(lastDot+1);
        }
    }



    @ExportMessage
    Object readMember(String member) {
        return new InteropType(component.findLocalType(getNamespace(member), getName(member)));
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    private boolean typeExists(String namespace, String name)
    {
        return component.findLocalType(namespace, name) != null;
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
        return typeExists(getNamespace(member), getName(member));
    }

    @ExportMessage
    Object getMembers(boolean includeInternal) {
        return new ConstStringArray(component.getAvailableTypes());
    }
}
