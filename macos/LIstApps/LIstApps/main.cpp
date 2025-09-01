//
//  main.cpp
//  LIstApps
//
//  Created by Icloud Bringardner on 8/30/25.
//

#include <CoreFoundation/CoreFoundation.h>
#include <ApplicationServices/ApplicationServices.h>
#include <iostream>

#import <Foundation/Foundation.h>
#import <UniformTypeIdentifiers/UniformTypeIdentifiers.h>
#include <iostream>

int main() {
    {
        // Replace with your desired file extension
        NSString *extension = @"jpg";

        // Create UTType from filename extension
        UTType *type = [UTType typeWithFilenameExtension:extension];

        if (type) {
            std::cout << "UTI for extension '.jpg': " << [type.identifier UTF8String] << std::endl;
        } else {
            std::cerr << "Could not determine UTI for extension." << std::endl;
        }
    }
    return 0;
}


int main4() {
    CFStringRef bundleID = CFSTR("com.apple.TextEdit"); // Replace with your bundle ID
    CFErrorRef error = nullptr;

    // Get array of application URLs for the bundle identifier
    CFArrayRef appURLs = LSCopyApplicationURLsForBundleIdentifier(bundleID, &error);

    if (!appURLs || CFArrayGetCount(appURLs) == 0) {
        std::cerr << "No application found for bundle identifier." << std::endl;
        if (error) {
            CFStringRef errDesc = CFErrorCopyDescription(error);
            char errBuffer[256];
            CFStringGetCString(errDesc, errBuffer, sizeof(errBuffer), kCFStringEncodingUTF8);
            std::cerr << "Error: " << errBuffer << std::endl;
            CFRelease(errDesc);
            CFRelease(error);
        }
        return 1;
    }

    // Get the first URL in the array
    CFURLRef appURL = (CFURLRef)CFArrayGetValueAtIndex(appURLs, 0);
    char path[PATH_MAX];
    if (CFURLGetFileSystemRepresentation(appURL, true, reinterpret_cast<UInt8*>(path), PATH_MAX)) {
        std::cout << "Application path: " << path << std::endl;
    } else {
        std::cerr << "Failed to get file system representation." << std::endl;
    }

    CFRelease(appURLs);
    return 0;
}

int main3() {
    // Replace with your desired bundle identifier
    CFStringRef bundleID = CFSTR("com.apple.TextEdit");

    // Get the URL for the application with the given bundle identifier
    CFURLRef appURL = nullptr;
    OSStatus status = LSFindApplicationForInfo(kLSUnknownCreator, bundleID, nullptr, nullptr, &appURL);

    if (status != noErr || appURL == nullptr) {
        std::cerr << "Failed to find application for bundle identifier." << std::endl;
        return 1;
    }

    // Convert URL to file system path
    char path[PATH_MAX];
    if (!CFURLGetFileSystemRepresentation(appURL, true, reinterpret_cast<UInt8*>(path), PATH_MAX)) {
        std::cerr << "Failed to convert URL to file system path." << std::endl;
        CFRelease(appURL);
        return 1;
    }

    std::cout << "Bundle path for identifier: " << path << std::endl;

    // Clean up
    CFRelease(appURL);
    return 0;
}

int main2(int argc, const char * argv[]) {
    CFStringRef        cfhandler = NULL;
    CFArrayRef        cfhandlers = NULL;
    CFStringRef uti = CFSTR("public.plain-text");
    // insert code here...
    std::cout << "Hello, World!\n";
    cfhandlers = LSCopyAllRoleHandlersForContentType(uti, kLSRolesAll );
    if( cfhandlers==NULL) {
        std::cout << "NULL\n";
    } else {
        std::cout << "NOT NULL\n";
        long count = CFArrayGetCount( cfhandlers );
        std::cout << count;
        std::cout << "\n";
        
        for (int i = 0; i < count; i++ ) {
            cfhandler = (CFStringRef) CFArrayGetValueAtIndex(cfhandlers,i);
            const char* str = CFStringGetCStringPtr(cfhandler, kCFStringEncodingMacRoman);
              
            std::cout <<str << "\n";
        }
    }
    return 0;
}
