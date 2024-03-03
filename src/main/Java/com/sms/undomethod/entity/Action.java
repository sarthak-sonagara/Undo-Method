package com.sms.undomethod.entity;
public class Action {
    private final int relativeStart;
    private final int length;
    private final CharSequence content;
    public static final Action noAction = new Action(0,0,"");

    public Action(int relativeStart, int length, CharSequence content) {
        this.relativeStart = relativeStart;
        this.length = length;
        this.content = content;
    }
    public int getRelativeStart() {
        return relativeStart;
    }
    public int getLength() {
        return length;
    }
    public CharSequence getContent() {
        return content;
    }

    public boolean isSingleInsertion() {
        return length==1 && content.isEmpty();
    }
    public boolean isSingleDeletion(){
        return length==0 && content.length()==1;
    }
}
