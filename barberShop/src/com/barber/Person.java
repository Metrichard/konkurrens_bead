package com.barber;

public class Person {
    private final String _name;
    private final Boolean _wantBeardTrim;
    private int _waitStarted;
    private int _waitEnded;

    //factory for person
    public Person(String name, Boolean wantsBeard, int waitStarted){
        _name = name;
        _wantBeardTrim = wantsBeard;
        _waitStarted = waitStarted;
    }

    public String GetName() {
        return _name;
    }

    public Boolean doesWantBeardTrim() {
        return _wantBeardTrim;
    }

    public void SetWaitEnded(int _waitEnded) {
        this._waitEnded = _waitEnded;
    }

    public int GetWaitStarted() {
        return _waitStarted;
    }

    public int GetWaitEnded() {
        return _waitEnded;
    }
}
