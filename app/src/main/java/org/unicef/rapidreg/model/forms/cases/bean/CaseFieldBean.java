package org.unicef.rapidreg.model.forms.cases.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class CaseFieldBean {
    @SerializedName("name")
    private String name;
    @SerializedName("editable")
    private boolean editable;
    @SerializedName("multi_select")
    private boolean multiSelect;
    @SerializedName("type")
    private String type;
    @SerializedName("display_name")
    private Map<String, String> displayName;
    @SerializedName("help_text")
    private Map<String, String> helpText;
    @SerializedName("option_strings_text")
    private Map<String, List> optionStringsText;
    @SerializedName("subform")
    private CaseSectionBean subForm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Map<String, String> displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getHelpText() {
        return helpText;
    }

    public void setHelpText(Map<String, String> helpText) {
        this.helpText = helpText;
    }

    public Map<String, List> getOptionStringsText() {
        return optionStringsText;
    }

    public void setOptionStringsText(Map<String, List> optionStringsText) {
        this.optionStringsText = optionStringsText;
    }

    public CaseSectionBean getSubForm() {
        return subForm;
    }

    public void setSubForm(CaseSectionBean subForm) {
        this.subForm = subForm;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Field>").append("\n");
        sb.append("name: ").append(name).append("\n");
        sb.append("editable: ").append(editable).append("\n");
        sb.append("multiSelect: ").append(multiSelect).append("\n");
        sb.append("type: ").append(type).append("\n");
        sb.append("displayName: ").append(displayName).append("\n");
        sb.append("helpText: ").append(helpText).append("\n");
        sb.append("optionStringsText: ").append(optionStringsText).append("\n");
        sb.append("subForm: ").append(subForm).append("\n");

        return sb.toString();
    }
}
