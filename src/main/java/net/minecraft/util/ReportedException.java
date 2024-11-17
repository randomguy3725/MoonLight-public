package net.minecraft.util;

import net.minecraft.crash.CrashReport;

public class ReportedException extends RuntimeException
{
    private final CrashReport theReportedExceptionCrashReport;

    public ReportedException(CrashReport report)
    {
        this.theReportedExceptionCrashReport = report;
    }

    public CrashReport getCrashReport()
    {
        return this.theReportedExceptionCrashReport;
    }

    public Throwable getCause()
    {
        return this.theReportedExceptionCrashReport.getCrashCause();
    }

    public String getMessage()
    {
        return this.theReportedExceptionCrashReport.getDescription();
    }
}
