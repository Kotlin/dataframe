classDiagram
direction LR
class dataSources
class reportController
class reportService
class springbootDataframeApplication

reportController  -->  reportService : depends on
reportService  -->  dataSources : depends on
springbootDataframeApplication  ..>  dataSources 
springbootDataframeApplication  ..>  reportController 
springbootDataframeApplication  ..>  reportService 
springbootDataframeApplication  ..>  springbootDataframeApplication 
