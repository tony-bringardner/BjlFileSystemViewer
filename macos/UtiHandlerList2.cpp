#include <ApplicationServices/ApplicationServices.h>
#include <CoreFoundation/CoreFoundation.h>

#include <sys/types.h>
#include <sys/param.h>
#include <sys/stat.h>
#include <dirent.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int uti_handler_show( char *uti, int showall )
{
    CFArrayRef		cfhandlers = NULL;
    CFStringRef		cfuti = NULL;
    CFStringRef		cfhandler = NULL;
    char		dh[ MAXPATHLEN ];
    int			rc = 0;
    int			count, i;

    if ( uti == NULL ) {
	fprintf( stderr, "Invalid UTI.\n" );
	return( 2 );
    }

    if ( c2cf( uti, &cfuti ) != 0 ) {
	return( 2 );
    }

    if ( showall ) {
	if (( cfhandlers = LSCopyAllRoleHandlersForContentType(
				cfuti, kLSRolesAll )) == NULL ) {
	    if (( cfhandlers = LSCopyAllHandlersForURLScheme(
					cfuti )) == NULL ) {
		fprintf( stderr, "%s: no handlers\n", uti );
		rc = 1;
		goto uti_show_done;
	    }
	}

	if ( verbose ) {
	    printf( "All handlers for %s:\n", uti );
	}

	count = CFArrayGetCount( cfhandlers );
	for ( i = 0; i < count; i++ ) {
	    cfhandler = CFArrayGetValueAtIndex( cfhandlers, i );
	    if ( cf2c( cfhandler, dh, sizeof( dh )) != 0 ) {
		rc = 2;
		continue;
	    }
	    printf( "%s\n", dh );
	    memset( dh, 0, sizeof( dh ));
	}
	
	cfhandler = NULL;
    } else {
	if (( cfhandler = LSCopyDefaultRoleHandlerForContentType(
				cfuti, kLSRolesAll )) == NULL ) {
	    if (( cfhandler = LSCopyDefaultHandlerForURLScheme(
					cfuti )) == NULL ) {
		fprintf( stderr, "%s: no default handler\n", uti );
		rc = 1;
		goto uti_show_done;
	    }
	}

	if ( cf2c( cfhandler, dh, MAXPATHLEN ) != 0 ) {
	    rc = 2;
	    goto uti_show_done;
	}

	if ( verbose ) {
	    printf( "Default handler for %s: ", uti );
	}
	printf( "%s\n", dh );
    }

uti_show_done:
    if ( cfhandlers ) {
	CFRelease( cfhandlers );
    }
    if ( cfuti ) {
	CFRelease( cfuti );
    }
    if ( cfhandler ) {
	CFRelease( cfhandler );
    }

    return( rc );
}
