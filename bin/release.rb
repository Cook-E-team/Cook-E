#!/usr/bin/env ruby

#
# release.rb: Prepares a Cook-E release and publishes it
#
# Usage: release.rb version
#
# version: A semantic version number, with an optional alpha, beta, or rc suffix
# and without a 'v' prefix
#

# Prints one or more lines of text with surrounding characters that make them
# stand out
def print_conspicuously(lines)
    puts '*' * 80
    lines.each do |line|
        print '* '
        puts line
    end
    puts '*' * 80
end

# Discards any changes in the repository, then exits
def cancel_release
    system 'git reset --hard HEAD'
    exit -1
end

# Modifies the build.gradle file at the provided path.
# Increments the version code and sets the version name to the provided version.
def update_build_file(path, version)

    version_code_regex = /^\s*versionCode\s+(\d+)$/
    version_name_regex = /^\s*versionName\s+"/

    lines = []

    File.open(path) do |file|
        lines = file.readlines
    end

    lines.map! do |line|
        version_code_match = version_code_regex.match line
        if version_code_match
            # Replace this line with the new version code
            old_code = version_code_match[1].to_i;
            new_code = old_code + 1;

            # Return the new line
            "        versionCode #{new_code}\n"
        elsif line =~ version_name_regex
            # Replace this line with the new version name
            "        versionName \"#{version}\"\n"
        else
            # Preserve this line
            line
        end
    end

    # Write the modified lines to the file
    File.open(path, File::WRONLY | File::TRUNC) do |file|
        lines.each do |line|
            file.write line
        end
    end
end

# Validate the version number
version = ARGV[0]

if !version || version.empty?
    print_conspicuously ['Please specify a version number.']
    exit -1
end

VERSION_REGEX = /^\d+.\d+.\d+(-(alpha|beta|rc)\d+)?$/

if !(version =~ VERSION_REGEX)
    print_conspicuously(
        ["#{version} is not a valid version number.",
        'Version numbers should not start with \'v\'.'])
    exit -1
end

# Get the absolute path of the repository root folder
repo_root = `git rev-parse --show-toplevel`

if !repo_root || repo_root.empty?
    print_conspicuously ['Could not find the repository root.',
        'Please ensure the current directory is in the project folder.']
    exit -1
end
# Remove whitespace
repo_root.strip!

# Validate git status
branch = `git rev-parse --abbrev-ref HEAD`
branch.strip!
if branch != 'master'
    print_conspicuously ["You are currently on branch #{branch}.",
        'Releases should normally be made from master.']
    print 'Continue? [y/N] '
    response = STDIN.gets.strip
    if response != 'Y' && response != 'y'
        print_conspicuously ['Cancelled']
        exit -1
    end
end

status = `git status --porcelain`
if !status.empty?
    print_conspicuously ['Your working directory is not clean. Please commit all your changes.']
    exit -1
end

# ==============================================================================
# Start making changes

# Update versions in build file
update_build_file("#{repo_root}/app/build.gradle", version)

# Run tests
print_conspicuously ['Testing...']

result = system "#{repo_root}/gradlew testReleaseUnitTest"

if !result
    print_conspicuously ['Testing failed - release cancelled']
    cancel_release
end

# Build APK
print_conspicuously ['Building...']
# Make a debug build
# Release builds appear to generate invalid APKs.
result = system "#{repo_root}/gradlew assembleDebug"

if !result
    print_conspicuously ['Build failed - release cancelled']
    cancel_release
end

# Give the APK the correct name
apk_path = "#{repo_root}/app/build/outputs/apk/Cook-E-#{version}.apk"
File.rename("#{repo_root}/app/build/outputs/apk/app-debug.apk",
    apk_path)


print_conspicuously ['Build succeeded', 'Committing and pushing version change']

# Commit changes
result = system "git add #{repo_root}/app/build.gradle"
if !result
    print_conspicuously ['Failed to add file - release cancelled']
    cancel_release
end
result = system "git commit -m 'Version increased to v#{version}'"
if !result
    print_conspicuously ['Failed to commit - release cancelled']
    cancel_release
end
# Make a tag
tag_name = "v#{version}"
result = system "git tag -a -m 'Version #{version}' #{tag_name}"
if !result
    print_conspicuously ['Failed to create tag - release cancelled']
    cancel_release
end
# Push
result = system "git push"
if !result
    print_conspicuously ['Failed to push - release cancelled']
    cancel_release
end
result = system "git push --tags"
if !result
    print_conspicuously ['Failed to push - release cancelled']
    cancel_release
end

# Display release information
print_conspicuously ['Now create a release: https://github.com/Cook-E-team/Cook-E/releases/new',
    "Tag: #{tag_name}",
    "Release title: #{tag_name}",
    "Upload the APK file at #{apk_path}"]
