//
//  OME2Tests.m
//  OME2Tests
//
//  Created by 新居雅行 on 2014/07/28.
//
//

#import <Cocoa/Cocoa.h>
#import <XCTest/XCTest.h>

@interface OME2Tests : XCTestCase

@end

@implementation OME2Tests

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testExample {
    // This is an example of a functional test case.
    XCTAssert(YES, @"Pass");
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
