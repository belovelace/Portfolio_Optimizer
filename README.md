# Portfolio_Optimizer
## 최종 발표 자료
(피그마 링크) https://www.figma.com/deck/G758gJraP55Y7l4jxbhx0z/%EB%8D%B0%EC%9D%B4%ED%84%B0%EC%B2%98%EB%A6%AC%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D?node-id=1-42&t=mhrlUZRvov5hElgR-1


## 커밋 템플릿
- feat: 새로운 기능 추가
- fix: 버그 수정
- docs: 문서 수정
- refactor: 코드 리펙토링
- test: 테스트 코드, 리펙토링 테스트 코드 추가
- chore: 설정 추가

## MVC 패턴
```

C:\dev\Portfolio_Optimizer\Portfolio_Optimizer\
├── app\src\main\
│   ├── java\com\portfoliolab\
│   │   ├── common\                       # 공통 모듈
│   │   │   ├── config\
│   │   │   │   ├── MyBatisConfig.java
│   │   │   │   └── DatabaseConfig.java
│   │   │   ├── exception\
│   │   │   └── util\
│   │   ├── domain\                       # 도메인별 구조
│   │   │   ├── stock\
│   │   │   │   ├── controller\
│   │   │   │   │   ├── StockController.java
│   │   │   │   │   └── StockSearchController.java
│   │   │   │   ├── service\
│   │   │   │   │   ├── StockService.java
│   │   │   │   │   └── StockSearchService.java
│   │   │   │   ├── mapper\               # MyBatis Mapper 인터페이스
│   │   │   │   │   ├── StockMapper.java
│   │   │   │   │   └── StockPriceMapper.java
│   │   │   │   ├── entity\
│   │   │   │   │   ├── Stock.java
│   │   │   │   │   └── StockPrice.java
│   │   │   │   └── dto\
│   │   │   │       ├── StockDto.java
│   │   │   │       └── StockSearchDto.java
│   │   │   ├── screening\
│   │   │   │   ├── controller\
│   │   │   │   ├── service\
│   │   │   │   ├── mapper\
│   │   │   │   │   └── MultifactorScreeningMapper.java
│   │   │   │   └── entity\
│   │   │   ├── correlation\
│   │   │   │   ├── controller\
│   │   │   │   ├── service\
│   │   │   │   ├── mapper\
│   │   │   │   │   └── CorrelationMapper.java
│   │   │   │   └── entity\
│   │   │   ├── portfolio\
│   │   │   │   ├── controller\
│   │   │   │   ├── service\
│   │   │   │   ├── mapper\
│   │   │   │   │   ├── PortfolioMapper.java
│   │   │   │   │   └── OptimizationMapper.java
│   │   │   │   └── entity\
│   │   │   └── dictionary\
│   │   │       ├── controller\
│   │   │       ├── service\
│   │   │       ├── mapper\
│   │   │       │   ├── StockTermMapper.java
│   │   │       │   └── TermCategoryMapper.java
│   │   │       └── entity\
│   │   └── PortfolioOptimizerApplication.java
│   └── resources\
│       ├── mapper\                       # MyBatis XML 매퍼 파일
│       │   ├── stock\
│       │   │   ├── StockMapper.xml
│       │   │   └── StockPriceMapper.xml
│       │   ├── screening\
│       │   │   └── MultifactorScreeningMapper.xml
│       │   ├── correlation\
│       │   │   └── CorrelationMapper.xml
│       │   ├── portfolio\
│       │   │   ├── PortfolioMapper.xml
│       │   │   └── OptimizationMapper.xml
│       │   └── dictionary\
│       │       ├── StockTermMapper.xml
│       │       └── TermCategoryMapper.xml
│       ├── application.yml
│       └── database\
│           ├── schema.sql
│           └── data.sql

```
