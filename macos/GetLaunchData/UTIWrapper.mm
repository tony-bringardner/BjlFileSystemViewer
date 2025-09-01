//
//  UTIWrapper.mm
//  GetLaunchData
//
//  Created by Icloud Bringardner on 8/31/25.
//

#import <Foundation/Foundation.h>
#import <UniformTypeIdentifiers/UniformTypeIdentifiers.h>
#include "UTIWrapper.hpp"

std::string UTIWrapper::getUTIFromExtension(const std::string& extension) {
    @autoreleasepool {
        NSString* ext = [NSString stringWithUTF8String:extension.c_str()];
        UTType* type = [UTType typeWithFilenameExtension:ext];

        if (type && type.identifier) {
            return std::string([type.identifier UTF8String]);
        } else {
            return "";
        }
    }
}
