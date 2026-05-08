package board;

public class Board {
    private int no;          // 글 번호
    private String title;    // 제목
    private String content;  // 내용
    private String writerId; // 작성자 아이디 (Member의 id)

    public Board(int no, String title, String content, String writerId) {
        this.no = no;
        this.title = title;
        this.content = content;
        this.writerId = writerId;
    }
    
    // Getter/Setter
	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWriterId() {
		return writerId;
	}

	public void setWriterId(String writerId) {
		this.writerId = writerId;
	}


    
}
