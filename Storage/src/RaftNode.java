import java.util.ArrayList;
import java.util.List;

enum State {
    FOLLOWER, CANDIDATE, LEADER
}

class LogEntry {
    public int term;
    public Object command;

    public LogEntry(int term, Object command) {
        this.term = term;
        this.command = command;
    }
}

public class RaftNode {
    private final int id;
    private final int numNodes;
    private int currentTerm;
    private int votedFor;
    private final List<Integer> votes;
    private List<LogEntry> log;
    private int commitIndex;
    private State state;
    private int leaderId;
    private List<Integer> nextIndex;
    private List<Integer> matchIndex;

    public RaftNode() {
        this.id = 0;
        this.numNodes = 0;
        this.currentTerm = 0;
        this.votedFor = -1;
        this.votes = new ArrayList<>();
        this.log = new ArrayList<>();
        this.commitIndex = 0;
        this.state = State.FOLLOWER;
        this.leaderId = -1;
        this.nextIndex = new ArrayList<>();
        this.matchIndex = new ArrayList<>();
    }

    public void becomeFollower(int term, int leaderId) {
        this.state = State.FOLLOWER;
        this.currentTerm = term;
        this.leaderId = leaderId;
        this.votedFor = -1;
    }

    public void becomeCandidate() {
        this.currentTerm++;
        this.state = State.CANDIDATE;
        this.votedFor = this.id;
    }

    public void becomeLeader() {
        this.state = State.LEADER;
        this.leaderId = this.id;
        this.nextIndex = new ArrayList<>();
        this.matchIndex = new ArrayList<>();
        for (int i = 0; i < this.numNodes; i++) {
            this.nextIndex.add(this.log.size());
            this.matchIndex.add(0);
        }
    }

    public void handleRequestVote(int term, int candidateId, int lastLogIndex, int lastLogTerm) {
        if (term < this.currentTerm) {
            sendResponse(candidateId, this.currentTerm, false);
            return;
        }
        if (term > this.currentTerm) {
            this.currentTerm = term;
            this.becomeFollower(term, candidateId);
        }
        if (this.votedFor == -1 || this.votedFor == candidateId) {
            int lastTerm = this.log.get(this.log.size() - 1).term;
            if (lastLogTerm > lastTerm || (lastLogTerm == lastTerm && lastLogIndex >= this.log.size() - 1)) {
                this.votedFor = candidateId;
                sendResponse(candidateId, this.currentTerm, true);
            } else {
                sendResponse(candidateId, this.currentTerm, false);
            }
        } else {
            sendResponse(candidateId, this.currentTerm, false);
        }
    }

    public void handleAppendEntries(int term, int leaderId, int prevLogIndex, int prevLogTerm, List<LogEntry> entries, int leaderCommit) {
        if (term < this.currentTerm) {
            sendResponse(leaderId, this.currentTerm, false);
            return;
        }
        if (term > this.currentTerm) {
            this.currentTerm = term;
            this.becomeFollower(term, leaderId);
        }
        if (this.state != State.FOLLOWER) {
            this.becomeFollower(term, leaderId);
        }
        if (prevLogIndex >= this.log.size() || this.log.get(prevLogIndex).term != prevLogTerm) {
            sendResponse(leaderId, this.currentTerm, false);
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            int index = prevLogIndex + i + 1;
            if (index >= this.log.size()) {
                this.log.add(entries.get(i));
            } else if (this.log.get(index).term != entries.get(i).term) {
                this.log = this.log.subList(0, index);
                this.log.add(entries.get(i));
            }
        }
        if (leaderCommit > this.commitIndex) {
            this.commitIndex = Math.min(leaderCommit, this.log.size() - 1);
        }
        sendResponse(leaderId, this.currentTerm, true);
    }

    public void handleClientRequest(Object command) {
        if (this.state == State.LEADER) {
            this.log.add(new LogEntry(this.currentTerm, command));
            for (int i = 0; i < this.numNodes; i++) {
                if (i != this.id) {
                    sendAppendEntries(i, this.currentTerm, this.id, this.log.size() - 1, this.log.get(this.log.size() - 1).term, this.log.subList(this.log.size() - 1, this.log.size()), this.commitIndex);
                }
            }
        }
        else {
            sendRequest(this.leaderId, command);
        }
    }

    public void handleTimeout() {
        if (this.state == State.FOLLOWER) {
            this.becomeCandidate();
            this.currentTerm++;
            int numVotes = 1;
            for (int i = 0; i < this.numNodes; i++) {
                if (i == this.id) {
                    continue;
                }
                int lastLogIndex = this.log.size() - 1;
                int lastLogTerm = lastLogIndex >= 0 ? this.log.get(lastLogIndex).term : -1;
                sendRequestVote(i, this.currentTerm, this.id, lastLogIndex, lastLogTerm);
            }
            if (numVotes > this.numNodes / 2) {
                this.becomeLeader();
            }
        } else if (this.state == State.LEADER) {
            for (int i = 0; i < this.numNodes; i++) {
                if (i == this.id) {
                    continue;
                }
                int prevLogIndex = this.nextIndex.get(i) - 1;
                int prevLogTerm = prevLogIndex >= 0 ? this.log.get(prevLogIndex).term : -1;
                List<LogEntry> entries = this.log.subList(this.nextIndex.get(i), this.log.size());
                sendAppendEntries(i, this.currentTerm, this.id, prevLogIndex, prevLogTerm, entries, this.commitIndex);
            }
        }
    }

    public void handleResponse(int senderId, int term, boolean voteGranted) {
        if (this.state == State.CANDIDATE && term == this.currentTerm) {
            if (voteGranted) {
                int numVotes = 1;
                for (int i = 0; i < this.numNodes; i++) {
                    if (i == this.id || this.votes.get(i) == null) {
                        continue;
                    }
                    numVotes++;
                }
                if (numVotes > this.numNodes / 2) {
                    this.becomeLeader();
                }
            } else if (this.state == State.LEADER && term == this.currentTerm) {
                if (voteGranted) {
                    this.matchIndex.set(senderId, this.nextIndex.get(senderId));
                    this.nextIndex.set(senderId, this.nextIndex.get(senderId) + 1);
                    int numMatched = 1;
                    for (int i = 0; i < this.numNodes; i++) {
                        if (i == this.id || this.matchIndex.get(i) < this.log.size() - 1) {
                            continue;
                        }
                        numMatched++;
                    }
                    if (numMatched > this.numNodes / 2) {
                        int newCommitIndex = this.log.size() - 1;
                        for (int i = this.commitIndex + 1; i <= newCommitIndex; i++) {
                            applyLogEntry(this.log.get(i));
                        }
                        this.commitIndex = newCommitIndex;
                    }
                } else {
                    this.nextIndex.set(senderId, Math.max(0, this.nextIndex.get(senderId) - 1));
                }
            }
        }
    }

    /*
    private void sendRequestVote() {
        for (int i = 0; i < this.numNodes; i++) {
            if (i == this.id) continue;
            int lastLogIndex = this.log.size() - 1;
            int lastLogTerm = this.log.get(lastLogIndex).term;
            sendRequestVote(i, this.currentTerm, this.id, lastLogIndex, lastLogTerm);
        }
    }

    private void sendAppendEntries() {
        for (int i = 0; i < this.numNodes; i++) {
            if (i == this.id) continue;
            int prevLogIndex = this.nextIndex.get(i) - 1;
            int prevLogTerm = this.log.get(prevLogIndex).term;
            List<LogEntry> entries = this.log.subList(this.nextIndex.get(i), this.log.size());
            sendAppendEntries(i, this.currentTerm, this.id, prevLogIndex, prevLogTerm, entries, this.commitIndex);
        }
    }
    */
    private void applyLogEntry(LogEntry entry) {
        // Apply log entry to state machine
    }

    public void sendResponse(int nodeId, int term, boolean voteGranted) {
        // Send response message to the given node
    }
    public void sendAppendEntries(int nodeId, int term, int leaderId, int prevLogIndex, int prevLogTerm, List<LogEntry> entries, int leaderCommit) {
        // Send AppendEntries message to the given node
    }
    public void sendRequestVote(int nodeId, int term, int candidateId, int lastLogIndex, int lastLogTerm) {
        // Send RequestVote message to the given node
    }
    public void sendRequest(int nodeId, Object command) {
        // Send Request message to the given node
    }
}
