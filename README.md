# Archeo4j
Archeology toolkit for java artifact.

## Why ?

You have created a huge SOA / micro services architecture want to answer question like
  - who is calling this method ?
  - who is declaring this class ?
  - who is using this artefact ?
  - do I have conflicting/duplicated classes in my web-inf/lib ?
  - what's the difference between these 2 wars ?
  - what's the difference in term of artifact's dependencies between my production and acceptance environments ?
  
## How ?

Archeo4j will parse the bytecode of your java artifacts and create a catalog, that you can query.

  - put all your artifacts (wars, jars) in a directory
  - archeo4j will process wars, bundled jars, classes and will create a catalog   
  - create a small java program or groovy script to answer your questions
  

## Sample usage

Have a look at [archeo4j-sample](https://github.com/mestachs/archeo4j/blob/master/archeo4j-sample/src/main/java/AnalyzeSonar.java)

 * duplicated classes Optional[org.codehaus.sonar:sonar-web:1.3 

```
org.apache.commons.logging.LogFactory (2) in [commons-logging:commons-logging:1.0.4 (WEB-INF/lib/commons-logging-1.0.4.jar), org.slf4j:jcl104-over-slf4j:1.4.3 (WEB-INF/lib/jcl104-over-slf4j-1.4.3.jar)]
	conflicting 6 methods 
		private static org.apache.commons.logging.LogFactory.cacheFactory(java.lang.ClassLoader,) not defined in : [org.slf4j:jcl104-over-slf4j:1.4.3 (WEB-INF/lib/jcl104-over-slf4j-1.4.3.jar)]
		private static org.apache.commons.logging.LogFactory.getCachedFactory(java.lang.ClassLoader) not defined in : [org.slf4j:jcl104-over-slf4j:1.4.3 (WEB-INF/lib/jcl104-over-slf4j-1.4.3.jar)]
		private static org.apache.commons.logging.LogFactory.getResourceAsStream(java.lang.ClassLoader,java.lang.String) not defined in : [org.slf4j:jcl104-over-slf4j:1.4.3 (WEB-INF/lib/jcl104-over-slf4j-1.4.3.jar)]
		protected static org.apache.commons.logging.LogFactory.getContextClassLoader() not defined in : [org.slf4j:jcl104-over-slf4j:1.4.3 (WEB-INF/lib/jcl104-over-slf4j-1.4.3.jar)]
		protected static org.apache.commons.logging.LogFactory.newFactory(java.lang.String,java.lang.ClassLoader) not defined in : [org.slf4j:jcl104-over-slf4j:1.4.3 (WEB-INF/lib/jcl104-over-slf4j-1.4.3.jar)]
		static org.apache.commons.logging.LogFactory.class$(java.lang.String) not defined in : [org.slf4j:jcl104-over-slf4j:1.4.3 (WEB-INF/lib/jcl104-over-slf4j-1.4.3.jar)]
```

 * who is declaring TendencyAnalyser
 
```
  org.codehaus.sonar:sonar-core:1.3 (WEB-INF/lib/sonar-core-1.3.jar)
	[AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getSumXPower2, signature=()Dparams=()], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSumXPower2, signature=(D)Vparams=(double)], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getSumYPower2, signature=()Dparams=()], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSumYPower2, signature=(D)Vparams=(double)], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getSumXY, signature=()Dparams=()], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSumXY, signature=(D)Vparams=(double)], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getYIntercept, signature=()Dparams=()], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setYIntercept, signature=(D)Vparams=(double)], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getSlope, signature=()Ljava/lang/Double;params=()], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSlope, signature=(Ljava/lang/Double;)Vparams=(java.lang.Double)], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getCorrelationRate, signature=()Ljava/lang/Double;params=()], AnalyzedMethod [org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setCorrelationRate, signature=(Ljava/lang/Double;)Vparams=(java.lang.Double)]]
```
 
 * who is using TendencyAnalyser from .*
 
```
public org.sonar.core.consolidation.tendency.TendencyAnalyser.getSlope() org.codehaus.sonar:sonar-core:1.3 (WEB-INF/lib/sonar-core-1.3.jar)
 calling :
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getSlope
public org.sonar.core.consolidation.tendency.TendencyAnalyser.getLevel() org.codehaus.sonar:sonar-core:1.3 (WEB-INF/lib/sonar-core-1.3.jar)
 calling :
	 org.sonar.core.consolidation.tendency.TendencyAnalyser.calculateLevel
protected org.sonar.core.consolidation.tendency.TendencyAnalyser.calculateLevel($SlopeData) org.codehaus.sonar:sonar-core:1.3 (WEB-INF/lib/sonar-core-1.3.jar)
 calling :
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getCorrelationRate
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.getSlope
protected org.sonar.core.consolidation.tendency.TendencyAnalyser.calculate(java.util.List) org.codehaus.sonar:sonar-core:1.3 (WEB-INF/lib/sonar-core-1.3.jar)
 calling :
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setYIntercept
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSlope
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSlope
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSlope
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSumXPower2
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSumXY
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setSumYPower2
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setCorrelationRate
	 org.sonar.core.consolidation.tendency.TendencyAnalyser$SlopeData.setCorrelationRate
protected org.sonar.core.consolidation.tendency.TendencyService.getLevel(org.sonar.core.consolidation.tendency.TendencyAnalyser) org.codehaus.sonar:sonar-core:1.3 (WEB-INF/lib/sonar-core-1.3.jar)
 calling :
	 org.sonar.core.consolidation.tendency.TendencyAnalyser.getLevel
```

* diff between production (sonar-web-1.4.2) and acceptance (sonar-web-1.3) ?

```
------------ DiffEntry [status=UPGRADED, before=org.codehaus.sonar:sonar-web:1.3 (archeo4j/archeo4j-sample/../../repo/prod/sonar-web-1.3.war), after=org.codehaus.sonar:sonar-web:1.4.2 (archeo4j/archeo4j-sample/../../repo/preprod/sonar-web-1.4.2.war)]
DiffEntry [status=REMOVED, before=, after=com.thoughtworks.xstream:xstream:1.3 (WEB-INF/lib/xstream-1.3.jar)]
DiffEntry [status=REMOVED, before=, after=xercesImpl:xercesImpl:2.8.1 (WEB-INF/lib/xercesImpl-2.8.1.jar)]
DiffEntry [status=REMOVED, before=, after=ejb3-persistence:ejb3-persistence:1.0.1.GA (WEB-INF/lib/ejb3-persistence-1.0.1.GA.jar)]
DiffEntry [status=REMOVED, before=, after=xpp3:xpp3:1.1.3.3 (WEB-INF/lib/xpp3-1.1.3.3.jar)]
DiffEntry [status=REMOVED, before=, after=org.codehaus.sonar:sonar-plugin-api:1.4.2 (WEB-INF/lib/sonar-plugin-api-1.4.2.jar)]
DiffEntry [status=REMOVED, before=, after=xml-apis:xml-apis:1.3.03 (WEB-INF/lib/xml-apis-1.3.03.jar)]
DiffEntry [status=REMOVED, before=, after=xpp3_min:xpp3_min:1.1.4c (WEB-INF/lib/xpp3_min-1.1.4c.jar)]
DiffEntry [status=UPGRADED, before=hibernate-annotations:hibernate-annotations:3.3.0.ga (WEB-INF/lib/hibernate-annotations-3.3.0.ga.jar), after=hibernate-annotations:hibernate-annotations:3.3.1.GA (WEB-INF/lib/hibernate-annotations-3.3.1.GA.jar)]
DiffEntry [status=UPGRADED, before=org.codehaus.sonar:sonar-core:1.3 (WEB-INF/lib/sonar-core-1.3.jar), after=org.codehaus.sonar:sonar-core:1.4.2 (WEB-INF/lib/sonar-core-1.4.2.jar)]
DiffEntry [status=UPGRADED, before=org.codehaus.sonar:sonar-commons:1.3 (WEB-INF/lib/sonar-commons-1.3.jar), after=org.codehaus.sonar:sonar-commons:1.4.2 (WEB-INF/lib/sonar-commons-1.4.2.jar)]
DiffEntry [status=UPGRADED, before=com.thoughtworks.paranamer:paranamer:1.1.2 (WEB-INF/lib/picocontainer-2.1.jar), after=com.thoughtworks.paranamer:paranamer:1.1.3 (WEB-INF/lib/picocontainer-2.3.jar)]
DiffEntry [status=UPGRADED, before=jruby-complete:jruby-complete:1.1.1 (WEB-INF/lib/jruby-complete-1.1.1.jar), after=jruby-complete:jruby-complete:1.1.2 (WEB-INF/lib/jruby-complete-1.1.2.jar)]
DiffEntry [status=UPGRADED, before=hibernate:hibernate:3.2.5.ga (WEB-INF/lib/hibernate-3.2.5.ga.jar), after=hibernate:hibernate:3.2.6.ga (WEB-INF/lib/hibernate-3.2.6.ga.jar)]
```
* Similar projects
  
  
  * https://github.com/benas/jcql
  * https://jqassistant.org/get-started/
