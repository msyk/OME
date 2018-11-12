//
//  MySpotlightImporter.h
//  OMESpotlightImporter
//
//  Created by 新居雅行 on 2014/09/03.
//  Copyright (c) 2014年 msyk.net. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@interface MySpotlightImporter : NSObject

@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;

- (BOOL)importFileAtPath:(NSString *)filePath attributes:(NSMutableDictionary *)attributes error:(NSError **)error;

@end
