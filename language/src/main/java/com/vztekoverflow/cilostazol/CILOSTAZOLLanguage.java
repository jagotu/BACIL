package com.vztekoverflow.cilostazol;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.vztekoverflow.cilostazol.runtime.CILOSTAZOLContext;

/**
 * The BACIL language class implementing TruffleLanguage.
 */
@TruffleLanguage.Registration(id = CILOSTAZOLLanguage.ID, name = CILOSTAZOLLanguage.NAME, interactive = false, defaultMimeType = CILOSTAZOLLanguage.CIL_PE_MIME_TYPE, byteMimeTypes = {CILOSTAZOLLanguage.CIL_PE_MIME_TYPE})
public class CILOSTAZOLLanguage extends TruffleLanguage<CILOSTAZOLContext> {

    public static final String ID = "cil";
    public static final String NAME = "CIL";

    public static final String CIL_PE_MIME_TYPE = "application/x-dosexec";

    private static final LanguageReference<CILOSTAZOLLanguage> REFERENCE = LanguageReference.create(CILOSTAZOLLanguage.class);

    public static CILOSTAZOLLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    @Override
    protected CILOSTAZOLContext createContext(Env env) {
        return new CILOSTAZOLContext(this, env);
    }
}
