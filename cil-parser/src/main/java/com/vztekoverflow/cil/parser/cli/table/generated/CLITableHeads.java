package com.vztekoverflow.cil.parser.cli.table.generated;

import com.vztekoverflow.cil.parser.cli.table.CLITableRow;
import com.vztekoverflow.cil.parser.cli.table.CLITables;

public class CLITableHeads {

  private final CLIModuleTableRow moduleTableHead;
  private final CLITypeRefTableRow typeRefTableHead;
  private final CLITypeDefTableRow typeDefTableHead;
  private final CLIFieldTableRow fieldTableHead;
  private final CLIMethodDefTableRow methodDefTableHead;
  private final CLIParamTableRow paramTableHead;
  private final CLIInterfaceImplTableRow interfaceImplTableHead;
  private final CLIMemberRefTableRow memberRefTableHead;
  private final CLIConstantTableRow constantTableHead;
  private final CLICustomAttributeTableRow customAttributeTableHead;
  private final CLIFieldMarshalTableRow fieldMarshalTableHead;
  private final CLIDeclSecurityTableRow declSecurityTableHead;
  private final CLIKlassLayoutTableRow klassLayoutTableHead;
  private final CLIFieldLayoutTableRow fieldLayoutTableHead;
  private final CLIStandAloneSigTableRow standAloneSigTableHead;
  private final CLIEventMapTableRow eventMapTableHead;
  private final CLIEventTableRow eventTableHead;
  private final CLIPropertyMapTableRow propertyMapTableHead;
  private final CLIPropertyTableRow propertyTableHead;
  private final CLIMethodSemanticsTableRow methodSemanticsTableHead;
  private final CLIMethodImplTableRow methodImplTableHead;
  private final CLIModuleRefTableRow moduleRefTableHead;
  private final CLITypeSpecTableRow typeSpecTableHead;
  private final CLIImplMapTableRow implMapTableHead;
  private final CLIFieldRVATableRow fieldRVATableHead;
  private final CLIAssemblyTableRow assemblyTableHead;
  private final CLIAssemblyProcessorTableRow assemblyProcessorTableHead;
  private final CLIAssemblyOSTableRow assemblyOSTableHead;
  private final CLIAssemblyRefTableRow assemblyRefTableHead;
  private final CLIAssemblyRefProcessorTableRow assemblyRefProcessorTableHead;
  private final CLIAssemblyRefOSTableRow assemblyRefOSTableHead;
  private final CLIFileTableRow fileTableHead;
  private final CLIExportedTypeTableRow exportedTypeTableHead;
  private final CLIManifestResourceTableRow manifestResourceTableHead;
  private final CLINestedKlassTableRow nestedKlassTableHead;
  private final CLIGenericParamTableRow genericParamTableHead;
  private final CLIMethodSpecTableRow methodSpecTableHead;
  private final CLIGenericParamConstraintTableRow genericParamConstraintTableHead;

  public CLITableHeads(CLITables tables) {
    int cursor = 0;
    moduleTableHead = new CLIModuleTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, moduleTableHead);
    typeRefTableHead = new CLITypeRefTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, typeRefTableHead);
    typeDefTableHead = new CLITypeDefTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, typeDefTableHead);
    fieldTableHead = new CLIFieldTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, fieldTableHead);
    methodDefTableHead = new CLIMethodDefTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, methodDefTableHead);
    paramTableHead = new CLIParamTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, paramTableHead);
    interfaceImplTableHead = new CLIInterfaceImplTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, interfaceImplTableHead);
    memberRefTableHead = new CLIMemberRefTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, memberRefTableHead);
    constantTableHead = new CLIConstantTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, constantTableHead);
    customAttributeTableHead = new CLICustomAttributeTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, customAttributeTableHead);
    fieldMarshalTableHead = new CLIFieldMarshalTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, fieldMarshalTableHead);
    declSecurityTableHead = new CLIDeclSecurityTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, declSecurityTableHead);
    klassLayoutTableHead = new CLIKlassLayoutTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, klassLayoutTableHead);
    fieldLayoutTableHead = new CLIFieldLayoutTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, fieldLayoutTableHead);
    standAloneSigTableHead = new CLIStandAloneSigTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, standAloneSigTableHead);
    eventMapTableHead = new CLIEventMapTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, eventMapTableHead);
    eventTableHead = new CLIEventTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, eventTableHead);
    propertyMapTableHead = new CLIPropertyMapTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, propertyMapTableHead);
    propertyTableHead = new CLIPropertyTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, propertyTableHead);
    methodSemanticsTableHead = new CLIMethodSemanticsTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, methodSemanticsTableHead);
    methodImplTableHead = new CLIMethodImplTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, methodImplTableHead);
    moduleRefTableHead = new CLIModuleRefTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, moduleRefTableHead);
    typeSpecTableHead = new CLITypeSpecTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, typeSpecTableHead);
    implMapTableHead = new CLIImplMapTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, implMapTableHead);
    fieldRVATableHead = new CLIFieldRVATableRow(tables, cursor, 0);
    cursor += getTableLength(tables, fieldRVATableHead);
    assemblyTableHead = new CLIAssemblyTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, assemblyTableHead);
    assemblyProcessorTableHead = new CLIAssemblyProcessorTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, assemblyProcessorTableHead);
    assemblyOSTableHead = new CLIAssemblyOSTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, assemblyOSTableHead);
    assemblyRefTableHead = new CLIAssemblyRefTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, assemblyRefTableHead);
    assemblyRefProcessorTableHead = new CLIAssemblyRefProcessorTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, assemblyRefProcessorTableHead);
    assemblyRefOSTableHead = new CLIAssemblyRefOSTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, assemblyRefOSTableHead);
    fileTableHead = new CLIFileTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, fileTableHead);
    exportedTypeTableHead = new CLIExportedTypeTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, exportedTypeTableHead);
    manifestResourceTableHead = new CLIManifestResourceTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, manifestResourceTableHead);
    nestedKlassTableHead = new CLINestedKlassTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, nestedKlassTableHead);
    genericParamTableHead = new CLIGenericParamTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, genericParamTableHead);
    methodSpecTableHead = new CLIMethodSpecTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, methodSpecTableHead);
    genericParamConstraintTableHead = new CLIGenericParamConstraintTableRow(tables, cursor, 0);
    cursor += getTableLength(tables, genericParamConstraintTableHead);
  }

  public static int getTableLength(CLITables tables, CLITableRow<?> row) {
    return row.getLength() * tables.getTablesHeader().getRowCount(row.getTableId());
  }

  public CLIModuleTableRow getModuleTableHead() {
    return moduleTableHead;
  }

  public CLITypeRefTableRow getTypeRefTableHead() {
    return typeRefTableHead;
  }

  public CLITypeDefTableRow getTypeDefTableHead() {
    return typeDefTableHead;
  }

  public CLIFieldTableRow getFieldTableHead() {
    return fieldTableHead;
  }

  public CLIMethodDefTableRow getMethodDefTableHead() {
    return methodDefTableHead;
  }

  public CLIParamTableRow getParamTableHead() {
    return paramTableHead;
  }

  public CLIInterfaceImplTableRow getInterfaceImplTableHead() {
    return interfaceImplTableHead;
  }

  public CLIMemberRefTableRow getMemberRefTableHead() {
    return memberRefTableHead;
  }

  public CLIConstantTableRow getConstantTableHead() {
    return constantTableHead;
  }

  public CLICustomAttributeTableRow getCustomAttributeTableHead() {
    return customAttributeTableHead;
  }

  public CLIFieldMarshalTableRow getFieldMarshalTableHead() {
    return fieldMarshalTableHead;
  }

  public CLIDeclSecurityTableRow getDeclSecurityTableHead() {
    return declSecurityTableHead;
  }

  public CLIKlassLayoutTableRow getKlassLayoutTableHead() {
    return klassLayoutTableHead;
  }

  public CLIFieldLayoutTableRow getFieldLayoutTableHead() {
    return fieldLayoutTableHead;
  }

  public CLIStandAloneSigTableRow getStandAloneSigTableHead() {
    return standAloneSigTableHead;
  }

  public CLIEventMapTableRow getEventMapTableHead() {
    return eventMapTableHead;
  }

  public CLIEventTableRow getEventTableHead() {
    return eventTableHead;
  }

  public CLIPropertyMapTableRow getPropertyMapTableHead() {
    return propertyMapTableHead;
  }

  public CLIPropertyTableRow getPropertyTableHead() {
    return propertyTableHead;
  }

  public CLIMethodSemanticsTableRow getMethodSemanticsTableHead() {
    return methodSemanticsTableHead;
  }

  public CLIMethodImplTableRow getMethodImplTableHead() {
    return methodImplTableHead;
  }

  public CLIModuleRefTableRow getModuleRefTableHead() {
    return moduleRefTableHead;
  }

  public CLITypeSpecTableRow getTypeSpecTableHead() {
    return typeSpecTableHead;
  }

  public CLIImplMapTableRow getImplMapTableHead() {
    return implMapTableHead;
  }

  public CLIFieldRVATableRow getFieldRVATableHead() {
    return fieldRVATableHead;
  }

  public CLIAssemblyTableRow getAssemblyTableHead() {
    return assemblyTableHead;
  }

  public CLIAssemblyProcessorTableRow getAssemblyProcessorTableHead() {
    return assemblyProcessorTableHead;
  }

  public CLIAssemblyOSTableRow getAssemblyOSTableHead() {
    return assemblyOSTableHead;
  }

  public CLIAssemblyRefTableRow getAssemblyRefTableHead() {
    return assemblyRefTableHead;
  }

  public CLIAssemblyRefProcessorTableRow getAssemblyRefProcessorTableHead() {
    return assemblyRefProcessorTableHead;
  }

  public CLIAssemblyRefOSTableRow getAssemblyRefOSTableHead() {
    return assemblyRefOSTableHead;
  }

  public CLIFileTableRow getFileTableHead() {
    return fileTableHead;
  }

  public CLIExportedTypeTableRow getExportedTypeTableHead() {
    return exportedTypeTableHead;
  }

  public CLIManifestResourceTableRow getManifestResourceTableHead() {
    return manifestResourceTableHead;
  }

  public CLINestedKlassTableRow getNestedKlassTableHead() {
    return nestedKlassTableHead;
  }

  public CLIGenericParamTableRow getGenericParamTableHead() {
    return genericParamTableHead;
  }

  public CLIMethodSpecTableRow getMethodSpecTableHead() {
    return methodSpecTableHead;
  }

  public CLIGenericParamConstraintTableRow getGenericParamConstraintTableHead() {
    return genericParamConstraintTableHead;
  }
}
