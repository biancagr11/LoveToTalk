package socialnetwork.domain;

public class FriendRequest extends Entity<Tuple<Long,Long>>{
    private String status;

    public FriendRequest(String status){this.status=status;}

    public String getStatus(){return status;}

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "FriendRequest{" +
                getId().getLeft()+
                "-->"+
                getId().getRight()+
                ": " + status + '\'' +
                '}';
    }
}
