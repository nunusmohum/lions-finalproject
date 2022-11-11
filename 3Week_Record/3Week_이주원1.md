# Title: [1Week] 이주원1

## 미션 요구사항 분석 & 체크리스트

### 필수과제

- [X] 관리자 회원
- [X] 관리자 페이지
- [X] 정산 데이터 생성
- [X] 건별 정산 처리
- [X] 전체 정산 처리

### 추가과제

- [X] 정산 데이터를 배치로 생성
  - [X] 스프링 내장 스케쥴러를 이용해서 배치가 매달 15일로 새벽 4시에 실행되도록
  - [X] @EnableScheduling 사용
  - [X] Quartz 사용금지, 스프링의 기본적인 스케쥴링 기능 사용
- [X] 출금 신청 기능(사용자 기능)
- [X] 출금 처리 기능(관리자 기능)

---

## 2주차 미션 요약

**[접근 방법]**
- 강사님의 2주차 코드를 베이스로 시작하였고 정산 및 배치에 관한 코드는 강의 실습때의 코드를 참고하였다.

- 정산 도메인과 스프링 배치에 익숙하지 않아 코드를 파악하는 데 시간을 많이 투자하였다.

**[특이사항]**
- 스프링 배치를 테스트 해보려고 Active profile에 값을 넣고 실행하니 자꾸 일부 환경변수 관련해서 오류가 났다. 처음에 원인을 몰라서 한참을 헤매다가 실행시 Active profiles에 값을 넣으면 application.yml의 active가 적용되지 않아 application-dev.yml의 환경 변수들이 포함되지 않는 것을 깨닫고 해결하였다.

- 배치 job의 makeRebateOrderItemJob이 initData 의존성 주입을 받아 실행하도록 되어 있는 코드가 있었는데 bean으로 등록된 initData가 주입이 되질 않아서 한참을 헤맸다. 생각해보니 Job을 굳이 실행 시에 실행할 필요가 없어서 batch.job.enabled 를 false 로 놓고 의존성을 제거하여 일단 해결하였다. 하지만 근본적인 원인은 찾지 못했다.
  
- RsData 클래스의 해당 메서드를 활용해보려 했는데 자꾸 ??? 물음표로 모달이 출력되었다. 도무지 이유를 알 수 없어서 일단 다른 방식으로 해결하였다.
  ```
  public String addMsgToUrl(String url) {
        if ( isFail() ) {
            return Ut.url.modifyQueryParam(url, "errorMsg", getMsg());
        }

        return Ut.url.modifyQueryParam(url, "msg", getMsg());
    }
  ```
  
- 이번 과제를 진행하면서 확실히 스프링 핵심 원리에 대한 이해가 많이 부족하다고 느꼈다. 스프링 빈과 관련된 핵심 개념들에 대해서 더 공부해야겠다고 생각하였다.

