package ar.edu.unq.eperdemic.spring.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Arrays.asList


@Aspect
@Component
class LoggingAspect {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    private val currentUsername: String
        get() {
            return  "System"
        }

    //This tells the aspect what is going to be intercepting. In this case, i'm telling it to
    //Intercept everything that is in the service package.

    //the proceedingJoinPoint intercepts and captures metadata that is meant for the real object being called
    //You can do what you want with that data, and at the end is necessary to call "proceed"
    // in order for the real method to be called

    //    @Around("@annotation(LogEntryAndArguments)")
    @Around("execution(public * ar.edu.unq.eperdemic.spring.controllers.*.*(..)))")
    @Throws(Throwable::class)
    fun logEntryAndArguementsAnnotation(proceedingJoinPoint: ProceedingJoinPoint): Any {

        val userName = currentUsername
        val method = getMethod(proceedingJoinPoint)
        val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val arguments = getArguments(proceedingJoinPoint)

        LOGGER.info("///////// \n  user {} called {}  at {}  \n with arguments: \n {} \n /////////", userName, method, timeStamp, arguments)
        return proceedingJoinPoint.proceed()
    }

    private fun getArguments(proceedingJoinPoint: ProceedingJoinPoint): String {
        val sb = StringBuilder()

        asList<Any>(*proceedingJoinPoint.args).stream().forEach { argument -> sb.append(" $argument") }

        return sb.toString()
    }

    private fun getMethod(proceedingJoinPoint: ProceedingJoinPoint): String {
        val methodSignature = proceedingJoinPoint.signature as MethodSignature
        return methodSignature.method.toString()
    }


}
