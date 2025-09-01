#include <ApplicationServices/ApplicationServices.h>
#include <CoreFoundation/CoreFoundation.h>

#include <iostream>

int main(int argc, const char * argv[]) {
    CFStringRef        cfhandler = NULL;
    CFArrayRef        cfhandlers = NULL;
    CFStringRef uti = CFSTR("public.text");
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
