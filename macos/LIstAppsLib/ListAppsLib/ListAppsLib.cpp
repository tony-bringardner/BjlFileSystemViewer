//
//  LIstAppsLib.cpp
//  LIstAppsLib
//
//  Created by Icloud Bringardner on 8/30/25.
//

#include <iostream>
#include "ListAppsLib.hpp"
#include "ListAppsLibPriv.hpp"

void ListAppsLib::HelloWorld(const char * s)
{
    ListAppsLibPriv *theObj = new ListAppsLibPriv;
    theObj->HelloWorldPriv(s);
    delete theObj;
};

void ListAppsLibPriv::HelloWorldPriv(const char * s) 
{
    std::cout << s << std::endl;
};

