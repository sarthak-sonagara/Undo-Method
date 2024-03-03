package com.sms.undomethod.entity;
public class Action {
    private final int relativeStart;
    private final CharSequence oldContent;
    private final CharSequence newContent;

    public int getRelativeStart() {
        return relativeStart;
    }

    public CharSequence getOldContent() {
        return oldContent;
    }

    public CharSequence getNewContent() {
        return newContent;
    }

    public Action(int relativeStart, CharSequence oldContent, CharSequence newContent) {
        this.relativeStart = relativeStart;
        this.oldContent = oldContent;
        this.newContent = newContent;
    }
}
