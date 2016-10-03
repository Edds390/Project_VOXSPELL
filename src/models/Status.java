package models;

/**
 * Created by edson on 15/09/16.
 */
public enum Status {
    Mastered(2),
    Faulted(1),
    Failed(0),
    Unseen(3);

    private int _statusNumber;

    Status(int number){
        _statusNumber = number;
    }

    public int getStatus(){
        return _statusNumber;
    }//-1 to account for failed starting at 1


}
