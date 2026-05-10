package board;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	// 데이터 저장소
	static ArrayList<Member> memberList = new ArrayList<>();
	static ArrayList<Board> boardList = new ArrayList<>();

	// 상태 관리 변수
	static Member loginUser = null; // 로그인 안 된 상태는 null
	static Scanner sc = new Scanner(System.in);
	static int boardCount = 1; // 글 번호 자동 증가용

	public static void main(String[] args) {
		loadData();
		while (true) {
			if (loginUser == null) {
				showGuestMenu();
			} else {
				showUserMenu();
			}
		}
	}
	
	
	// 숫자 입력 받는 함수
	static int inputNumber() {
	    while (true) {
	        try {
	            // 숫자를 입력받고 개행 문자(엔터)까지 비움
	            String input = sc.next();
	            return Integer.parseInt(input); 
	        } catch (NumberFormatException e) {
	            // 숫자가 아닌 값이 들어왔을 때 실행되는 구간
	            System.out.print("숫자만 입력 가능합니다. 다시 입력해주세요");
	            System.out.println("\n== [메인 메뉴] ==");
	    		System.out.print("1.로그인 2.회원가입 0.종료 > ");
	        }
	    }
	}
	// 로그인 전 메뉴
	static void showGuestMenu() {
		System.out.println("\n== [메인 메뉴] ==");
		System.out.print("1.로그인 2.회원가입 0.종료 > ");
		int choice = inputNumber();
		// switch-case 연결
		switch (choice) {
		case 1:
			login();
			break;
		case 2:
			join();
			break;
		case 0:
			saveData();
			System.out.println("프로그램을 종료합니다.");
		    System.exit(0);
		}
	}

	// 로그인 후 메뉴
	static void showUserMenu() {
		System.out.println("\n== [" + loginUser.getName() + "님 접속 중] ==");
		System.out.print("1.목록보기 2.글쓰기 3.로그아웃 0.종료 > ");
		int choice = inputNumber();
		// login 실행 후 writerId에 loginUser.getId() 가능
		switch (choice) {
		case 1:
			listPosts();
			break;
		case 2:
			writePost();
			break;
		case 3:
			loginUser = null;
			System.out.println("로그아웃 합니다.");
			break;
		case 0:
			saveData();
			System.out.println("프로그램을 종료합니다.");
		    System.exit(0);
		}
	}
	
	// 비밀번호 검증
	static boolean isValidPassword(String pw) {
	    // 8자리 이상, 영문, 숫자, 특수문자 조합 패턴
	    String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
	    return pw.matches(regex);
	}
	// 아이디 검증
	static boolean isValidId(String id) {
	    // 영문 또는 숫자로 이루어진 6자 이상
	    return id.matches("^[a-zA-Z0-9]{6,}$");
	}
	// 이름 검증
	static boolean isValidName(String name) {
	    // 한글 또는 영문만 허용 (숫자나 특수문자 포함 시 false)
	    return name.matches("^[가-힣a-zA-Z\\s]+$");
	}
	
	// 회원가입
	static void join() {
		System.out.println("\n--- [회원가입] ---");
		
		String id = "";
	    String pw = "";
	    String name = "";

		// [1관문] 아이디 입력 (형식 + 중복 체크)
	    while (true) {
	        System.out.print("아이디 입력(6자 이상): ");
	        id = sc.next();
	        if (isValidId(id)) {
	            // 중복 체크
	            boolean isDup = false;
	            for (Member m : memberList) {
	                if (m.getId().equals(id)) { isDup = true; break; }
	            }
	            if (!isDup) break; // 통과!
	            else System.out.println("이미 존재하는 아이디입니다.");
	        } else {
	            System.out.println("[오류] 영문/숫자 조합 6자 이상이어야 합니다.");
	        }
	    }

	    // [2관문] 비밀번호 입력 (조합 체크)
	    while (true) {
	        System.out.print("비밀번호 입력(8자 이상, 영문/숫자/특수문자): ");
	        pw = sc.next();
	        if (isValidPassword(pw)) break; // 통과!
	        else System.out.println("[오류] 비밀번호 형식이 올바르지 않습니다.");
	    }

	    // [3관문] 이름 입력 (문자 체크)
	    while (true) {
	        System.out.print("이름 입력: ");
	        name = sc.next();
	        if (isValidName(name)) break; // 통과!
	        else System.out.println("[오류] 이름에 숫자나 특수문자를 넣을 수 없습니다.");
	    }

	    // [최종] 모든 관문 통과 후 가입 처리
	    memberList.add(new Member(id, pw, name));
	    System.out.println("\n" + name + "님, 회원가입이 완료되었습니다!");
	}

	// 로그인
	static void login() {
		System.out.println("\n--- [로그인] ---");
		System.out.print("아이디: ");
		String inputId = sc.next();
		System.out.print("비밀번호: ");
		String inputPw = sc.next();

		// memberList에 저장된 회원들 중 일치하는 사람이 있는지 확인
		for (Member m : memberList) {
			// 아이디와 비밀번호가 모두 일치해야 함
			if (m.getId().equals(inputId) && m.getPw().equals(inputPw)) {
				loginUser = m; // 로그인 성공한 회원을 전역 변수에 저장
				System.out.println("\n" + loginUser.getName() + "님, 환영합니다!");
				return; // 로그인 성공 시 함수 즉시 종료
			}
		}

		// 반복문을 다 돌았는데도 return되지 않았다면 정보가 틀린것
		System.out.println("아이디 또는 비밀번호가 일치하지 않습니다.");
	}
	
	// 글쓰기
	static void writePost() {
	    if (loginUser == null) {
	        System.out.println("로그인 후 이용 가능합니다.");
	        return;
	    }

	    System.out.println("\n--- [새 글 쓰기] ---");
	    sc.nextLine(); // 이전 입력에서 남은 엔터값(버퍼) 제거
	    
	    System.out.print("제목: ");
	    String title = sc.nextLine();
	    
	    System.out.print("내용: ");
	    String content = sc.nextLine();

	    // 게시글 객체 생성: 작성자는 현재 로그인한 유저의 ID를 자동으로 할당
	    Board newPost = new Board(boardCount++, title, content, loginUser.getId());
	    boardList.add(newPost);

	    System.out.println(boardCount - 1 + "번 게시글이 등록되었습니다.");
	}
	
	// 글보기
	static void listPosts() {
		while(true) {
			System.out.println("\n--- [ 전체 게시글 목록 ] ---");
			
			if (boardList.isEmpty()) {
				System.out.println("등록된 게시글이 없습니다. 첫 글의 주인공이 되어보세요!");
				return;
			}
			
			System.out.println("번호 | 제목 | 작성자");
			System.out.println("---------------------------------");
			
			// 리스트의 마지막 인덱스(size-1)부터 0까지 거꾸로 반복
			for (int i = boardList.size() - 1; i >= 0; i--) {
				Board b = boardList.get(i);
				System.out.printf("%d | %s | %s\n", b.getNo(), b.getTitle(), b.getWriterId());
			}
			
			System.out.print("\n상세보기 글 번호(메인메뉴로 돌아가기 0): ");
			int targetNo = sc.nextInt();
			
			if (targetNo == 0) {
				System.out.println("메인 메뉴로 돌아갑니다.");
				return; // 현재 함수(listPosts)를 즉시 종료하고 호출한 곳으로 돌아감
			}
			
			viewPostById(targetNo);
		}
	}
	
	// 상세보기
	static void viewPostById(int targetNo) {
	    Board foundBoard = null;
	    for (Board b : boardList) {
	        if (b.getNo() == targetNo) {
	            foundBoard = b;
	            break;
	        }
	    }

	    if (foundBoard == null) {
	        System.out.println("존재하지 않는 글 번호입니다.");
	        return;
	    }

	    // 상세 내용 출력
	    System.out.println("\n---------------------------");
	    System.out.println("제목: " + foundBoard.getTitle());
	    System.out.println("내용: " + foundBoard.getContent());
	    System.out.println("---------------------------");

	    // 본인 확인 로직
	    if (foundBoard.getWriterId().equals(loginUser.getId())) {
	        System.out.print("1.수정 2.삭제 0.목록으로 > ");
	        int menu = inputNumber();
	        
	        if (menu == 1) {
	        	// 상세 페이지에서 수정
	            updatePost(foundBoard);
	        } else if (menu == 2) {
	        	// 상세 페이지에서 삭제
	            deletePost(foundBoard);
	        }
	    } else {
	        System.out.println("(본인의 글이 아니면 수정/삭제할 수 없습니다)");
	        System.out.print("0.목록으로 > ");
	        inputNumber(); // 0을 누르면 끝남 (목록으로 돌아감)
	    }
	}
	
	// 수정
	static void updatePost(Board b) {
	    System.out.println("\n--- [ 게시글 수정 ] ---");
	    sc.nextLine(); // 버퍼 비우기
	    
	    System.out.print("새 제목: ");
	    b.setTitle(sc.nextLine()); // Setter를 활용해 기존 객체 값 수정
	    
	    System.out.print("새 내용: ");
	    b.setContent(sc.nextLine());
	    
	    System.out.println("글이 수정되었습니다.");
	}
	
	// 삭제
	static void deletePost(Board b) {
	    boardList.remove(b); // 리스트에서 해당 객체 삭제
	    System.out.println("글이 삭제되었습니다.");
	}
	
	// 파일저장(DB임시) - FILE I/O 방식
	// 파일저장
	static void saveData() {
		
		// 1. 멤버 저장 (데이터가 있을 때만)
	    if (!memberList.isEmpty()) {
	        try (ObjectOutputStream outMember = new ObjectOutputStream(new FileOutputStream("members.dat"))) {
	            outMember.writeObject(memberList);
	            System.out.println("[시스템] 회원 데이터 저장 완료.");
	        } catch (IOException e) {
	            System.out.println("[오류] 회원 저장 실패: " + e.getMessage());
	        }
	    }

	    // 2. 게시글 저장 (데이터가 있을 때만)
	    if (!boardList.isEmpty()) {
	        try (ObjectOutputStream outBoard = new ObjectOutputStream(new FileOutputStream("boards.dat"))) {
	            outBoard.writeObject(boardList);
	            System.out.println("[시스템] 게시글 데이터 저장 완료.");
	        } catch (IOException e) {
	            System.out.println("[오류] 게시글 저장 실패: " + e.getMessage());
	        }
	    }
	    
	}

	// 파일불러오기
	static void loadData() {
		// 멤버 파일이 있으면 불러오기
	    File mFile = new File("members.dat");
	    if (mFile.exists()) {
	        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(mFile))) {
	            memberList = (ArrayList<Member>) in.readObject();
	        } catch (Exception e) {
	            System.out.println("[오류] 회원 불러오기 실패");
	        }
	    }

	    // 게시글 파일이 있으면 불러오기
	    File bFile = new File("boards.dat");
	    if (bFile.exists()) {
	        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(bFile))) {
	            boardList = (ArrayList<Board>) in.readObject();
	            // 글 번호 동기화
	            if (!boardList.isEmpty()) {
	                boardCount = boardList.get(boardList.size() - 1).getNo() + 1;
	            }
	        } catch (Exception e) {
	            System.out.println("[오류] 게시글 불러오기 실패");
	        }
	    }
	}
}
