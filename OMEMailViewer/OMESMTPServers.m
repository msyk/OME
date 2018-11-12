//
//  $Id: OMESMTPServers.m,v 1.3 2007/10/02 13:45:36 msyk Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Tue Jan 18 15:41:55 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMESMTPServers.h"

#import "OMELines.h"
#import "OMESMTPServer.h"

@implementation OMESMTPServers

/* accessor method */
- (void)setArray:(NSArray *)anArray
{
    array = anArray;
}

/* accessor method */
- (NSArray *)array
{
     return array;
}

/* designated initializer */
- (id)initWithArray:(NSArray *)anArray
{
    self = [ super init ];
    if ( self ) {
        [ self setArray:anArray ];
    }
    return self;
}

/* initializer */
- (id)initWithString:(NSString *)aString
{
    NSUInteger count, i;
    NSArray *anArray, *lineArray;
    NSString *nameString, *addressString, *smtpString, *authString;
    OMELines *omeLines;
    OMESMTPServer *server;

    /* separate to lines from string */
    omeLines = [ OMELines linesWithString:aString ];
    lineArray = [ omeLines array ];
    count = [ lineArray count ];
    anArray = [ NSArray array ];
	
	int countSet = count / 4;
	
    for ( i = 0; i < countSet; i++ ) {
        nameString = [ lineArray objectAtIndex:i*4 ];
        addressString = [ lineArray objectAtIndex:( i*4 + 1 ) ];
        smtpString = [ lineArray objectAtIndex:( i*4 + 2 ) ];
        authString = [ lineArray objectAtIndex:( i*4 + 3 ) ];
        server = [ [ OMESMTPServer alloc ]
                     initWithName:nameString
                     address:addressString
                     smtp:smtpString
                     authString:authString ];
        anArray = [ anArray arrayByAddingObject:server ];
    }
    /* return */
    return [ self initWithArray:anArray ];
}

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath
{
    NSString *aString;
NSError *error;
    aString = [ NSString stringWithContentsOfFile:aPath  encoding: NSUTF8StringEncoding error:&error /**/];
    return [ self initWithString:aString ];
}

/* dealloc */

/* description of smtp server */
- (NSString *)description
{
    NSEnumerator *aEnumerator;
    NSString *aString, *aDescription;
    OMESMTPServer *server;

    aEnumerator = [ [ self array ] objectEnumerator ];
    aString = @"";
    while ( ( server = [ aEnumerator nextObject ] ) != nil ) {
        aDescription = [ server description ];
        aString = [ aString stringByAppendingString:aDescription ];
    }
    return aString;
}

/* Addresses */
- (NSArray *)addresses
{
    NSArray *anArray;
    NSEnumerator *aEnumerator;
    NSString *aString;
    OMESMTPServer *server;

    anArray = [ NSArray array ];
    aEnumerator = [ [ self array ] objectEnumerator ];
    while ( ( server = [ aEnumerator nextObject ] ) != nil ) {
        aString = [ server address ];
        anArray = [ anArray arrayByAddingObject:aString ];
    }
    return anArray;
}

@end
