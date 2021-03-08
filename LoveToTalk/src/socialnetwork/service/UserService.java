package socialnetwork.service;


import socialnetwork.config.observer.Observable;
import socialnetwork.config.observer.Observer;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.FriendRequestDatabase;
import socialnetwork.repository.database.FriendshipDatabase;
import socialnetwork.repository.database.MessageDatabase;
import socialnetwork.repository.database.UserDatabase;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class UserService implements Observable{
    private UserDatabase repoUsers;
    private FriendshipDatabase repoFriendship;
    MessageDatabase repoMessage;
    private FriendRequestDatabase repoFriendRequest;

    public UserService(UserDatabase repoUsers, FriendshipDatabase repoFriendship,
                       FriendRequestDatabase repoFriendRequest,MessageDatabase repoMessage) {
        this.repoUsers = repoUsers;
        this.repoFriendship=repoFriendship;
        this.repoMessage=repoMessage;
        for(Friendship p: repoFriendship.findAll()){
            Tuple<Long,Long> id=p.getId();
            User friend1=repoUsers.findOne(id.getLeft());
            User friend2=repoUsers.findOne(id.getRight());
            friend1.addFriend(friend2);
            friend2.addFriend(friend1);
        }
        this.repoFriendRequest=repoFriendRequest;
    }

    @Override
    public void addObserver(Observer e){
        observers.add(e);
    }
    @Override
    public void removeObserver(Observer e){

    }
    @Override
    public void notifyObservers(){
        observers.stream().forEach(x->x.update());
    }

    public User addUser(User messageTask) {
        User task = repoUsers.save(messageTask);
        return task;
    }

    public User deleteUser(Long id) {
        Queue<Tuple<Long,Long>> q=new LinkedList<Tuple<Long,Long>>();
        for(Friendship p: repoFriendship.findAll()){
            Tuple<Long,Long> idP=p.getId();
            if(idP.getRight()==id || idP.getLeft()==id){
                User friend1=repoUsers.findOne(idP.getLeft());
                User friend2=repoUsers.findOne(idP.getRight());
                friend1.removeFriend(friend2);
                friend2.removeFriend(friend1);
                q.add(idP);
            }
        }
        while(!q.isEmpty()){
            repoFriendship.delete(q.remove());
        }
        User response=repoUsers.delete(id);
        return response;
    }
    public Friendship addFriendship(Friendship p){
        Tuple<Long,Long> id=p.getId();
        User friend1=repoUsers.findOne(id.getLeft());
        User friend2=repoUsers.findOne(id.getRight());
        if(friend1==null)
            throw new ValidationException("Invalid friend 1!\n");
        if(friend2==null)
            throw new ValidationException("Invalid friend 2!\n");
        Friendship response=repoFriendship.save(p);
        friend1.addFriend(friend2);
        friend2.addFriend(friend1);
        notifyObservers();
        return response;
    }
    public Friendship removeFriendship(Tuple<Long,Long> id) {
        Friendship response=repoFriendship.delete(id);
        User friend1=repoUsers.findOne(id.getLeft());
        User friend2=repoUsers.findOne(id.getRight());
        if(friend1==null)
            throw new ValidationException("Invalid friend 1!\n");
        if(friend2==null)
            throw new ValidationException("Invalid friend 2!\n");
        friend1.removeFriend(friend2);
        friend2.removeFriend(friend1);
        notifyObservers();
        return response;
    }

    public User findUser(Long id){return repoUsers.findOne(id);}

    public Iterable<User> findAllUsers(){
        return repoUsers.findAll();
    }

    public Iterable<Friendship> findAllFriendships(){
        return repoFriendship.findAll();
    }

    public Iterable<User> getAll(){
        return repoUsers.findAll();
    }

    private void DFS(Vector<Vector<Integer>>graph, int source, Vector<Boolean>visited, Vector<Integer> path){
        for (int i=0; i < graph.size(); i++)
        {
            if (graph.get(source).get(i) == 1 && !visited.get(i)) {
                visited.setElementAt(true,i);
                path.setElementAt(source,i);
                DFS(graph, i, visited,path);
            }
        }
    }

    private int maxDistance(Vector<Vector<Integer>>graph, int source){
        Vector<Integer> dist=new Vector<Integer>();
        for(int i=0;i< graph.size();i++) {
            dist.add(0);
        }
        //dist.setElementAt(1,source);

        Vector<Boolean> visited=new Vector<Boolean>();
        for(int i=0;i< graph.size();i++){
            visited.add(false);
        }

        Queue<Integer> q= new LinkedList<Integer>();
        q.add(source);
        visited.setElementAt(true,source);
        while(!q.isEmpty())
        {
            int t=q.remove();
            for(int i=0;i<graph.size();i++){
                if(graph.get(t).get(i) == 1 && !visited.get(i)){
                        q.add(i);
                        visited.setElementAt(true,i);
                        dist.setElementAt(dist.get(t)+1,i);
                }
            }
        }
        int maxim=0;
        for(int i=0;i< graph.size();i++){
            if(dist.get(i)>maxim){
                maxim=dist.get(i);
            }
        }
        return maxim;
    }

    public int nrComunitati(){
        Iterable<Friendship> list=repoFriendship.findAll();
        long max=0;
        for(User u: repoUsers.findAll()){
            max=Long.max(u.getId(),max);
        }
//        for(Prietenie p: list){
//            max=Long.max(p.getId().getRight(),max);
//            max=Long.max(p.getId().getLeft(),max);
//        }
        Vector<Vector<Integer>> matrix=new Vector<Vector<Integer>>();
        for(int i=0;i<=max;i++){
            Vector<Integer> row=new Vector<Integer>();
            for(int j=0;j<=max;j++){
                row.add(0);
            }
            matrix.add(row);
        }
        for(Friendship p: list){
            int left=Integer.parseInt(p.getId().getLeft().toString());
            int right=Integer.parseInt(p.getId().getRight().toString());
            matrix.get(left).setElementAt(1,right);
            matrix.get(right).setElementAt(1,left);
        }

        Vector<Integer> path=new Vector<Integer>();
        for(int i=0;i<=max;i++){
            path.add(-1);
        }
        Vector<Boolean> inGraph=new Vector<Boolean>();
        for(int i=0;i<=max;i++){
            inGraph.add(false);
        }
        for(User u: repoUsers.findAll()){
            int index=Integer.parseInt(u.getId().toString());
            inGraph.setElementAt(true,index);
        }
        int nrComp=0;
        Vector<Boolean> visited=new Vector<Boolean>();
        for(int i=0;i<=max;i++){
            visited.add(false);
        }
        for(int i=0;i<=max;i++){
            if(visited.get(i)==false && inGraph.get(i)){
                DFS(matrix,i,visited,path);
                nrComp++;
            }
        }
        return nrComp;
    }

    public Vector<Integer> biggestCommunity(){

        Iterable<Friendship> list=repoFriendship.findAll();
        long max=0;
        for(Friendship p: list){
            max=Long.max(p.getId().getRight(),max);
            max=Long.max(p.getId().getLeft(),max);
        }

        Vector<Integer> path=new Vector<Integer>();
        for(int i=0;i<=max;i++) {
            path.add(-1);
        }

        Vector<Vector<Integer>> matrix=new Vector<Vector<Integer>>();
        for(int i=0;i<=max;i++){
            Vector<Integer> row=new Vector<Integer>();
            for(int j=0;j<=max;j++){
                row.add(0);
            }
            matrix.add(row);
        }
        Vector<Boolean> inGraph=new Vector<Boolean>();
        for(int i=0;i<=max;i++){
            inGraph.add(false);
        }
        for(Friendship p: list){
            int left=Integer.parseInt(p.getId().getLeft().toString());
            int right=Integer.parseInt(p.getId().getRight().toString());
            matrix.get(left).setElementAt(1,right);
            matrix.get(right).setElementAt(1,left);
            inGraph.setElementAt(true,left);
            inGraph.setElementAt(true,right);
        }
        Vector<Boolean> visited=new Vector<Boolean>();
        for(int i=0;i<=max;i++){
            visited.add(false);
        }
        int partialMax=0,node=0,maximum=0;
        for(int i=0;i<=max;i++) {
            if (inGraph.get(i) == true) {
                partialMax=maxDistance(matrix,i);
                if(partialMax>maximum){
                    maximum=partialMax;
                    node=i;
                }
            }
        }
        DFS(matrix,node,visited,path);
        Vector<Integer> nodes=new Vector<Integer>();
        for(int i=0;i<=max;i++){
            if(path.get(i)!=-1){
                nodes.add(i);
            }
        }
        return nodes;

    }

    public Optional<String> usersFriends(Long id){

        User vrf=repoUsers.findOne(id);
        if(vrf==null){
            throw new ValidationException("User does not exist!\n");
        }
        Iterable<Friendship> friendships=repoFriendship.findAll();
        List<Friendship> list=new ArrayList<Friendship>();
        friendships.forEach(list::add);
        Optional<String> opFriendships=list.stream()
                .filter(x->{
                    return x.getId().getLeft()==id || id==x.getId().getRight();
                })
                .map(x->{
                    User user=null;
                    if(x.getId().getLeft()!=id){
                        user=repoUsers.findOne(x.getId().getLeft());
                    }
                    else {
                        user=repoUsers.findOne(x.getId().getRight());
                    }
                    return user.getLastName()+" | "+user.getFirstName()+" | "+
                            x.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+"\n";
                })
                .reduce((a,b)->a+b);
        return opFriendships;
    }

    public Optional<String> usersFriendsByDate(Long id, Long month){

        User vrf=repoUsers.findOne(id);
        Month localMonth = Month.of(Integer.parseInt(month.toString()));
        if(vrf==null){
            throw new ValidationException("User does not exist!\n");
        }
        Iterable<Friendship> friendships=repoFriendship.findAll();
        List<Friendship> list=new ArrayList<Friendship>();
        friendships.forEach(list::add);
        Optional<String> opFriendships=list.stream()
                .filter(x->{
                    return (x.getId().getLeft()==id || id==x.getId().getRight()) && localMonth==x.getDate().getMonth();
                })
                .map(x->{
                    User user=null;
                    if(x.getId().getLeft()!=id){
                        user=repoUsers.findOne(x.getId().getLeft());
                    }
                    else {
                        user=repoUsers.findOne(x.getId().getRight());
                    }
                    return user.getLastName()+" | "+user.getFirstName()+" | "+
                             x.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+"\n";
                })
                .reduce((a,b)->a+b);
        return opFriendships;
    }

    public Iterable<ReplyMessage> findAllMessages(){return repoMessage.findAll();}

    public ReplyMessage addMessage(Long fromID, List<Long> toIDS, String text, LocalDateTime date){
        User from=repoUsers.findOne(fromID);
        if(from==null)
            throw new ValidationException("~From user~ is not valid!");
        List<User> toList=new ArrayList<User>();
        for(Long id:toIDS){
            if(fromID==id)
                throw new ValidationException("Can not send a message to yourself!\n");
            User to=repoUsers.findOne(id);
            if(to==null)
                throw new ValidationException("User "+id+" does not exist!");
            Tuple<Long,Long> tuple=new Tuple<Long,Long>(fromID,id);
            if(repoFriendship.findOne(tuple)==null){
                throw new ValidationException("There is not friendship between "+ fromID + " and "+id);
            }
            toList.add(to);
        }
        ReplyMessage message=new ReplyMessage(from, toList,text,date,null);
        ReplyMessage response= repoMessage.save(message);
        this.notifyObservers();
        return response;

    }

    public ReplyMessage replyMessage(Long fromID, String text, LocalDateTime date, Long idToReply){
        User from=repoUsers.findOne(fromID);
        if(from==null)
            throw new ValidationException("~From user~ is not valid!");
        List<User> toList=new ArrayList<User>();
        Message messageToReply=repoMessage.findOne(idToReply);
        if(messageToReply==null){
            throw new ValidationException("This message does not exist!\n");
        }
        if(messageToReply.getFrom().getId()==fromID)
            throw new ValidationException("Can not send a message to yourself!\n");
        Boolean exists=false;
        for(User user:messageToReply.getTo()){
            if(user.getId()==fromID){
                exists=true;
                break;
            }
        }
        if(exists==false){
            throw new ValidationException("This user can not reply to this message!\n");
        }
        toList.add(messageToReply.getFrom());
        ReplyMessage message=new ReplyMessage(from, toList,text,date,messageToReply);
        return repoMessage.save(message);

    }

    public List<ReplyMessage>conversation(Long id1, Long id2){
        Iterable<ReplyMessage> allMessages=repoMessage.findAll();
        List<ReplyMessage> convs=new ArrayList<ReplyMessage>();
        for(ReplyMessage msg:allMessages){
            List<User> to=msg.getTo();
            List<Long> longIds = to.stream()
                    .filter(x->{
                        return ((x.getId()==id1 && msg.getFrom().getId()==id2) || (x.getId()==id2 && msg.getFrom().getId()==id1));
                    })
                    .map(x->{
                        return x.getId();
                    })
                    .collect(Collectors.toList());
            if(!longIds.isEmpty())
                if((msg.getFrom().getId()==id1 && longIds.get(0)==id2) || (msg.getFrom().getId()==id2 && longIds.get(0)==id1))
                    convs.add(msg);

            }
        return convs.stream().sorted((x,y)->x.getDate().compareTo(y.getDate())).collect(Collectors.toList());
    }

    //FriendRequests

    public Iterable<FriendRequest> allFriendRequests(){return repoFriendRequest.findAll();}

    public FriendRequest addFriendRequest(Long id1, Long id2){
        User friend1=repoUsers.findOne(id1);
        User friend2=repoUsers.findOne(id2);
        if(friend1==null)
            throw new ValidationException("Invalid friend 1!\n");
        if(friend2==null)
            throw new ValidationException("Invalid friend 2!\n");
        Tuple<Long, Long> id=new Tuple<Long, Long>(id1, id2);
        Friendship friendship= repoFriendship.findOne(id);
        if(friendship!=null)
            throw new ValidationException(friend2.getFirstName()+" is already your friend!\n");
        FriendRequest friendRequest= repoFriendRequest.findOne(id);
        if(friendRequest!=null)
            throw new ValidationException("There is a pending request between you and "+friend2.getFirstName()+"!");
        FriendRequest toAdd=new FriendRequest("pending");
        toAdd.setId(id);
        FriendRequest response=repoFriendRequest.save(toAdd);
        notifyObservers();
        return response;
    }
    public void updateFriendRequest(Long id1, Long id2, String newStatus){
        User friend1=repoUsers.findOne(id1);
        User friend2=repoUsers.findOne(id2);
        if(friend1==null)
            throw new ValidationException("Invalid friend 1!\n");
        if(friend2==null)
            throw new ValidationException("Invalid friend 2!\n");
        Tuple<Long, Long> id=new Tuple<Long, Long>(id1, id2);
        Friendship friendship= repoFriendship.findOne(id);
        if(friendship!=null)
            throw new ValidationException(id1+" and "+id2+" are already friends!\n");
        repoFriendRequest.update(id,newStatus);
        if(newStatus.equals("approved")) {
            Friendship friendshipAdd = new Friendship(LocalDateTime.now());
            friendshipAdd.setId(id);
            addFriendship(friendshipAdd);
            repoFriendRequest.delete(id);
        }
        if(newStatus.equals("rejected"))
            repoFriendRequest.delete(id);
    }

    public FriendRequest deleteFriendRequest(Long id1, Long id2){
        FriendRequest response=repoFriendRequest.delete(new Tuple<Long,Long>(id1,id2));
        notifyObservers();
        return response;

    }

    public List<FriendRequestDTO> userRequests(Long id){
        Iterable<FriendRequest> friendships=repoFriendRequest.findAll();
        List<FriendRequest> list=new ArrayList<FriendRequest>();
        friendships.forEach(list::add);
        List<FriendRequestDTO> listFriendRequests=list.stream()
                .filter(x->{
                    return x.getId().getRight()==id;
                })
                .map(x->{
                    User user= repoUsers.findOne(x.getId().getLeft());
                    return new FriendRequestDTO(user.getId(),user.getFirstName()+" "+user.getLastName(),x.getStatus());
                })
                .collect(Collectors.toList());
        return listFriendRequests;
    }

    public List<FriendRequestDTO> sentRequests(Long id){
        Iterable<FriendRequest> friendships=repoFriendRequest.findAll();
        List<FriendRequest> list=new ArrayList<FriendRequest>();
        friendships.forEach(list::add);
        List<FriendRequestDTO> listFriendRequests=list.stream()
                .filter(x->{
                    return x.getId().getLeft()==id;
                })
                .map(x->{
                    User user= repoUsers.findOne(x.getId().getRight());
                    return new FriendRequestDTO(user.getId(),user.getFirstName()+" "+user.getLastName(),x.getStatus());
                })
                .collect(Collectors.toList());
        return listFriendRequests;
    }


}
