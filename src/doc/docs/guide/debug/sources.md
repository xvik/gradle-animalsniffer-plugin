# Source sets debug

!!! note
This task is useful for debugging plugin behavior. initially, it was written during
android and kotlin multiplatform support development to see what sources and tasks
are present in the (android) project.

    Task preserved in release in case of incorrect sources registration for animalsniffer 
    tasks (so output could be assigned to the issue and help with investigation).

    Moreover, task relies on deprecated android api and most likely would be removed when 
    such api would disappear. 

`printAnimalsnifferSourceInfo` task prints:

1. Registered plugins
2. Compile tasks (hierarchy): all tasks with "compile" or "classes" in name
3. Java source sets (if java plugin registered)
4. Android variants and source sets (old api used)
5. Multiplatform platforms, compilations and source sets

Example report for android project:

```
== [Plugins] ===============================================================

	Plugins of potential interest ------------------------------------------ (5)
		<no id>                                                            com.android.build.gradle.api.AndroidBasePlugin
		com.android.application                  application               com.android.build.gradle.AppPlugin
		com.android.internal.application         application               com.android.build.gradle.internal.plugins.AppPlugin
		<no id>                                                            org.gradle.api.plugins.JavaBasePlugin
		com.android.internal.version-check       version-check             com.android.build.gradle.internal.plugins.VersionCheckPlugin

	Other plugins ----------------------------------------------------------- (9)
		ru.vyarus.animalsniffer                  animalsniffer             ru.vyarus.gradle.plugin.animalsniffer.AnimalSnifferPlugin
		<no id>                                                            org.gradle.api.plugins.BasePlugin
		org.gradle.build-init                    build-init                org.gradle.buildinit.plugins.BuildInitPlugin
		org.gradle.help-tasks                    help-tasks                org.gradle.api.plugins.HelpTasksPlugin
		<no id>                                                            org.gradle.api.plugins.JvmEcosystemPlugin
		<no id>                                                            org.gradle.api.plugins.JvmToolchainsPlugin
		<no id>                                                            org.gradle.language.base.plugins.LifecycleBasePlugin
		<no id>                                                            org.gradle.api.plugins.ReportingBasePlugin
		org.gradle.wrapper                       wrapper                   org.gradle.buildinit.plugins.WrapperPlugin


== [Compile Tasks] ===============================================================

	Tasks containing 'compile' in name ------------------------------------ (46)

		compileDebugAidl                         com.android.build.gradle.tasks.AidlCompile
		compileDebugAndroidTestAidl              com.android.build.gradle.tasks.AidlCompile
		compileReleaseAidl                       com.android.build.gradle.tasks.AidlCompile
		debugAndroidTestAnimalsnifferClassesCollector ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
		debugAnimalsnifferClassesCollector       ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
		debugUnitTestAnimalsnifferClassesCollector ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
		releaseAnimalsnifferClassesCollector     ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
		releaseUnitTestAnimalsnifferClassesCollector ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
		bundleDebugAndroidTestClassesToCompileJar com.android.build.gradle.internal.feature.BundleAllClasses
		bundleDebugAndroidTestClassesToRuntimeJar com.android.build.gradle.internal.feature.BundleAllClasses
		bundleDebugClassesToCompileJar           com.android.build.gradle.internal.feature.BundleAllClasses
		bundleDebugClassesToRuntimeJar           com.android.build.gradle.internal.feature.BundleAllClasses
		bundleDebugUnitTestClassesToCompileJar   com.android.build.gradle.internal.feature.BundleAllClasses
		bundleDebugUnitTestClassesToRuntimeJar   com.android.build.gradle.internal.feature.BundleAllClasses
		bundleReleaseClassesToCompileJar         com.android.build.gradle.internal.feature.BundleAllClasses
		bundleReleaseClassesToRuntimeJar         com.android.build.gradle.internal.feature.BundleAllClasses
		bundleReleaseUnitTestClassesToCompileJar com.android.build.gradle.internal.feature.BundleAllClasses
		bundleReleaseUnitTestClassesToRuntimeJar com.android.build.gradle.internal.feature.BundleAllClasses
		checkDebugAndroidTestDuplicateClasses    com.android.build.gradle.internal.tasks.CheckDuplicateClassesTask
		checkDebugDuplicateClasses               com.android.build.gradle.internal.tasks.CheckDuplicateClassesTask
		checkReleaseDuplicateClasses             com.android.build.gradle.internal.tasks.CheckDuplicateClassesTask
		compileDebugArtProfile                   com.android.build.gradle.internal.tasks.CompileArtProfileTask
		compileReleaseArtProfile                 com.android.build.gradle.internal.tasks.CompileArtProfileTask
		compileDebugAndroidTestSources           org.gradle.api.DefaultTask
		compileDebugSources                      org.gradle.api.DefaultTask
		compileDebugUnitTestSources              org.gradle.api.DefaultTask
		compileLintChecks                        org.gradle.api.DefaultTask
		compileReleaseSources                    org.gradle.api.DefaultTask
		compileReleaseUnitTestSources            org.gradle.api.DefaultTask
		compileDebugAndroidTestJavaWithJavac     org.gradle.api.tasks.compile.JavaCompile
		compileDebugJavaWithJavac                org.gradle.api.tasks.compile.JavaCompile
		compileDebugUnitTestJavaWithJavac        org.gradle.api.tasks.compile.JavaCompile
		compileReleaseJavaWithJavac              org.gradle.api.tasks.compile.JavaCompile
		compileReleaseUnitTestJavaWithJavac      org.gradle.api.tasks.compile.JavaCompile
		javaPreCompileDebug                      com.android.build.gradle.tasks.JavaPreCompileTask
		javaPreCompileDebugAndroidTest           com.android.build.gradle.tasks.JavaPreCompileTask
		javaPreCompileDebugUnitTest              com.android.build.gradle.tasks.JavaPreCompileTask
		javaPreCompileRelease                    com.android.build.gradle.tasks.JavaPreCompileTask
		javaPreCompileReleaseUnitTest            com.android.build.gradle.tasks.JavaPreCompileTask
		compileLint                              com.android.build.gradle.internal.tasks.LintCompile
		compileDebugAndroidTestRenderscript      com.android.build.gradle.tasks.RenderscriptCompile
		compileDebugRenderscript                 com.android.build.gradle.tasks.RenderscriptCompile
		compileReleaseRenderscript               com.android.build.gradle.tasks.RenderscriptCompile
		compileDebugAndroidTestShaders           com.android.build.gradle.tasks.ShaderCompile
		compileDebugShaders                      com.android.build.gradle.tasks.ShaderCompile
		compileReleaseShaders                    com.android.build.gradle.tasks.ShaderCompile

	Compile tasks tree -------------------------------------------------------- (28 roots)

		[debugAndroidTestAnimalsnifferClassesCollector] ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
			[compileDebugAndroidTestJavaWithJavac]   org.gradle.api.tasks.compile.JavaCompile
				[compileDebugAndroidTestAidl]            com.android.build.gradle.tasks.AidlCompile
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateDebugAndroidTestBuildConfig      (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileDebugAndroidTest]         com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				processDebugAndroidTestResources         (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileDebugAndroidTestRenderscript]    com.android.build.gradle.tasks.RenderscriptCompile
					processDebugAndroidTestManifest          (com.android.build.gradle.tasks.ProcessTestManifest)
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)

		[debugAnimalsnifferClassesCollector]     ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
			[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
				[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[debugUnitTestAnimalsnifferClassesCollector] ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
			[compileDebugUnitTestJavaWithJavac]      org.gradle.api.tasks.compile.JavaCompile
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileDebugUnitTest]            com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugUnitTestBuild                    (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[releaseAnimalsnifferClassesCollector]   ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
			[compileReleaseJavaWithJavac]            org.gradle.api.tasks.compile.JavaCompile
				[compileReleaseAidl]                     com.android.build.gradle.tasks.AidlCompile
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateReleaseBuildConfig               (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileRelease]                  com.android.build.gradle.tasks.JavaPreCompileTask
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				processReleaseResources                  (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileReleaseRenderscript]             com.android.build.gradle.tasks.RenderscriptCompile
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[releaseUnitTestAnimalsnifferClassesCollector] ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
			[compileReleaseUnitTestJavaWithJavac]    org.gradle.api.tasks.compile.JavaCompile
				[bundleReleaseClassesToCompileJar]       com.android.build.gradle.internal.feature.BundleAllClasses
					[compileReleaseJavaWithJavac]            org.gradle.api.tasks.compile.JavaCompile
						[compileReleaseAidl]                     com.android.build.gradle.tasks.AidlCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateReleaseBuildConfig               (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileRelease]                  com.android.build.gradle.tasks.JavaPreCompileTask
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processReleaseResources                  (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileReleaseRenderscript]             com.android.build.gradle.tasks.RenderscriptCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileReleaseUnitTest]          com.android.build.gradle.tasks.JavaPreCompileTask
					preReleaseUnitTestBuild                  (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[bundleDebugAndroidTestClassesToCompileJar] com.android.build.gradle.internal.feature.BundleAllClasses
			[compileDebugAndroidTestJavaWithJavac]   org.gradle.api.tasks.compile.JavaCompile
				[compileDebugAndroidTestAidl]            com.android.build.gradle.tasks.AidlCompile
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateDebugAndroidTestBuildConfig      (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileDebugAndroidTest]         com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				processDebugAndroidTestResources         (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileDebugAndroidTestRenderscript]    com.android.build.gradle.tasks.RenderscriptCompile
					processDebugAndroidTestManifest          (com.android.build.gradle.tasks.ProcessTestManifest)
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)

		[bundleDebugAndroidTestClassesToRuntimeJar] com.android.build.gradle.internal.feature.BundleAllClasses
			[compileDebugAndroidTestJavaWithJavac]   org.gradle.api.tasks.compile.JavaCompile
				[compileDebugAndroidTestAidl]            com.android.build.gradle.tasks.AidlCompile
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateDebugAndroidTestBuildConfig      (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileDebugAndroidTest]         com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				processDebugAndroidTestResources         (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileDebugAndroidTestRenderscript]    com.android.build.gradle.tasks.RenderscriptCompile
					processDebugAndroidTestManifest          (com.android.build.gradle.tasks.ProcessTestManifest)
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)

		[bundleDebugClassesToRuntimeJar]         com.android.build.gradle.internal.feature.BundleAllClasses
			[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
				[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[bundleDebugUnitTestClassesToCompileJar] com.android.build.gradle.internal.feature.BundleAllClasses
			[compileDebugUnitTestJavaWithJavac]      org.gradle.api.tasks.compile.JavaCompile
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileDebugUnitTest]            com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugUnitTestBuild                    (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[bundleDebugUnitTestClassesToRuntimeJar] com.android.build.gradle.internal.feature.BundleAllClasses
			[compileDebugUnitTestJavaWithJavac]      org.gradle.api.tasks.compile.JavaCompile
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileDebugUnitTest]            com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugUnitTestBuild                    (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[bundleReleaseClassesToRuntimeJar]       com.android.build.gradle.internal.feature.BundleAllClasses
			[compileReleaseJavaWithJavac]            org.gradle.api.tasks.compile.JavaCompile
				[compileReleaseAidl]                     com.android.build.gradle.tasks.AidlCompile
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateReleaseBuildConfig               (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileRelease]                  com.android.build.gradle.tasks.JavaPreCompileTask
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				processReleaseResources                  (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileReleaseRenderscript]             com.android.build.gradle.tasks.RenderscriptCompile
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[bundleReleaseUnitTestClassesToCompileJar] com.android.build.gradle.internal.feature.BundleAllClasses
			[compileReleaseUnitTestJavaWithJavac]    org.gradle.api.tasks.compile.JavaCompile
				[bundleReleaseClassesToCompileJar]       com.android.build.gradle.internal.feature.BundleAllClasses
					[compileReleaseJavaWithJavac]            org.gradle.api.tasks.compile.JavaCompile
						[compileReleaseAidl]                     com.android.build.gradle.tasks.AidlCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateReleaseBuildConfig               (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileRelease]                  com.android.build.gradle.tasks.JavaPreCompileTask
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processReleaseResources                  (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileReleaseRenderscript]             com.android.build.gradle.tasks.RenderscriptCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileReleaseUnitTest]          com.android.build.gradle.tasks.JavaPreCompileTask
					preReleaseUnitTestBuild                  (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[bundleReleaseUnitTestClassesToRuntimeJar] com.android.build.gradle.internal.feature.BundleAllClasses
			[compileReleaseUnitTestJavaWithJavac]    org.gradle.api.tasks.compile.JavaCompile
				[bundleReleaseClassesToCompileJar]       com.android.build.gradle.internal.feature.BundleAllClasses
					[compileReleaseJavaWithJavac]            org.gradle.api.tasks.compile.JavaCompile
						[compileReleaseAidl]                     com.android.build.gradle.tasks.AidlCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateReleaseBuildConfig               (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileRelease]                  com.android.build.gradle.tasks.JavaPreCompileTask
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processReleaseResources                  (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileReleaseRenderscript]             com.android.build.gradle.tasks.RenderscriptCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileReleaseUnitTest]          com.android.build.gradle.tasks.JavaPreCompileTask
					preReleaseUnitTestBuild                  (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[checkDebugAndroidTestDuplicateClasses]  com.android.build.gradle.internal.tasks.CheckDuplicateClassesTask
			preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)

		[checkDebugDuplicateClasses]             com.android.build.gradle.internal.tasks.CheckDuplicateClassesTask
			preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[checkReleaseDuplicateClasses]           com.android.build.gradle.internal.tasks.CheckDuplicateClassesTask
			preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[compileDebugArtProfile]                 com.android.build.gradle.internal.tasks.CompileArtProfileTask
			preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
			mergeExtDexDebug                         (com.android.build.gradle.internal.tasks.DexMergingTask)
			mergeLibDexDebug                         (com.android.build.gradle.internal.tasks.DexMergingTask)
			mergeProjectDexDebug                     (com.android.build.gradle.internal.tasks.DexMergingTask)
			mergeDebugArtProfile                     (com.android.build.gradle.internal.tasks.MergeArtProfileTask)

		[compileReleaseArtProfile]               com.android.build.gradle.internal.tasks.CompileArtProfileTask
			preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
			mergeDexRelease                          (com.android.build.gradle.internal.tasks.DexMergingTask)
			mergeReleaseArtProfile                   (com.android.build.gradle.internal.tasks.MergeArtProfileTask)

		[compileDebugAndroidTestSources]         org.gradle.api.DefaultTask
			[compileDebugAndroidTestJavaWithJavac]   org.gradle.api.tasks.compile.JavaCompile
				[compileDebugAndroidTestAidl]            com.android.build.gradle.tasks.AidlCompile
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateDebugAndroidTestBuildConfig      (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileDebugAndroidTest]         com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)
				processDebugAndroidTestResources         (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileDebugAndroidTestRenderscript]    com.android.build.gradle.tasks.RenderscriptCompile
					processDebugAndroidTestManifest          (com.android.build.gradle.tasks.ProcessTestManifest)
					preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)

		[compileDebugSources]                    org.gradle.api.DefaultTask
			[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
				[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
					preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[compileDebugUnitTestSources]            org.gradle.api.DefaultTask
			[compileDebugUnitTestJavaWithJavac]      org.gradle.api.tasks.compile.JavaCompile
				[bundleDebugClassesToCompileJar]         com.android.build.gradle.internal.feature.BundleAllClasses
					[compileDebugJavaWithJavac]              org.gradle.api.tasks.compile.JavaCompile
						[compileDebugAidl]                       com.android.build.gradle.tasks.AidlCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateDebugBuildConfig                 (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileDebug]                    com.android.build.gradle.tasks.JavaPreCompileTask
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processDebugResources                    (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileDebugRenderscript]               com.android.build.gradle.tasks.RenderscriptCompile
							preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileDebugUnitTest]            com.android.build.gradle.tasks.JavaPreCompileTask
					preDebugUnitTestBuild                    (com.android.build.gradle.internal.tasks.AndroidVariantTask)
			processDebugJavaRes                      (com.android.build.gradle.internal.tasks.ProcessJavaResTask)
			processDebugUnitTestJavaRes              (com.android.build.gradle.internal.tasks.ProcessJavaResTask)

		[compileLintChecks]                      org.gradle.api.DefaultTask

		[compileReleaseSources]                  org.gradle.api.DefaultTask
			[compileReleaseJavaWithJavac]            org.gradle.api.tasks.compile.JavaCompile
				[compileReleaseAidl]                     com.android.build.gradle.tasks.AidlCompile
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				generateReleaseBuildConfig               (com.android.build.gradle.tasks.GenerateBuildConfig)
				[javaPreCompileRelease]                  com.android.build.gradle.tasks.JavaPreCompileTask
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				processReleaseResources                  (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
				[compileReleaseRenderscript]             com.android.build.gradle.tasks.RenderscriptCompile
					preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)

		[compileReleaseUnitTestSources]          org.gradle.api.DefaultTask
			[compileReleaseUnitTestJavaWithJavac]    org.gradle.api.tasks.compile.JavaCompile
				[bundleReleaseClassesToCompileJar]       com.android.build.gradle.internal.feature.BundleAllClasses
					[compileReleaseJavaWithJavac]            org.gradle.api.tasks.compile.JavaCompile
						[compileReleaseAidl]                     com.android.build.gradle.tasks.AidlCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						generateReleaseBuildConfig               (com.android.build.gradle.tasks.GenerateBuildConfig)
						[javaPreCompileRelease]                  com.android.build.gradle.tasks.JavaPreCompileTask
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
						processReleaseResources                  (com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask)
						[compileReleaseRenderscript]             com.android.build.gradle.tasks.RenderscriptCompile
							preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
				[javaPreCompileReleaseUnitTest]          com.android.build.gradle.tasks.JavaPreCompileTask
					preReleaseUnitTestBuild                  (com.android.build.gradle.internal.tasks.AndroidVariantTask)
			processReleaseJavaRes                    (com.android.build.gradle.internal.tasks.ProcessJavaResTask)
			processReleaseUnitTestJavaRes            (com.android.build.gradle.internal.tasks.ProcessJavaResTask)

		[compileLint]                            com.android.build.gradle.internal.tasks.LintCompile

		[compileDebugAndroidTestShaders]         com.android.build.gradle.tasks.ShaderCompile
			mergeDebugAndroidTestShaders             (com.android.build.gradle.tasks.MergeSourceSetFolders)
			preDebugAndroidTestBuild                 (com.android.build.gradle.internal.tasks.TestPreBuildTask)

		[compileDebugShaders]                    com.android.build.gradle.tasks.ShaderCompile
			preDebugBuild                            (com.android.build.gradle.internal.tasks.AndroidVariantTask)
			mergeDebugShaders                        (com.android.build.gradle.tasks.MergeSourceSetFolders)

		[compileReleaseShaders]                  com.android.build.gradle.tasks.ShaderCompile
			preReleaseBuild                          (com.android.build.gradle.internal.tasks.AndroidVariantTask)
			mergeReleaseShaders                      (com.android.build.gradle.tasks.MergeSourceSetFolders)

	Java compile tasks --------------------------------------------- (5)

		[compileDebugAndroidTestJavaWithJavac] -----

			Sources
				build/generated/aidl_source_output_dir/debugAndroidTest/out                      NOT EXISTS
				build/generated/renderscript_source_output_dir/debugAndroidTest/out              NOT EXISTS
				build/generated/source/buildConfig/androidTest/debug                             NOT EXISTS
				src/androidTest/java                                                             NOT EXISTS
				src/androidTestDebug/java                                                        NOT EXISTS

			Output
				build/generated/ap_generated_sources/debugAndroidTest/out
				build/intermediates/javac/debugAndroidTest/classes
				build/tmp/compileDebugAndroidTestJavaWithJavac/previous-compilation-data.bin

			Classpath
				build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debugAndroidTest/R.jar
				build/intermediates/compile_app_classes_jar/debug/classes.jar
				slf4j-api-1.7.25.jar

		[compileDebugJavaWithJavac] -----

			Sources
				build/generated/aidl_source_output_dir/debug/out                                 NOT EXISTS
				build/generated/renderscript_source_output_dir/debug/out                         NOT EXISTS
				build/generated/source/buildConfig/debug                                         NOT EXISTS
				src/debug/java                                                                   NOT EXISTS
				src/main/java

			Output
				build/generated/ap_generated_sources/debug/out
				build/intermediates/javac/debug/classes
				build/tmp/compileDebugJavaWithJavac/previous-compilation-data.bin

			Classpath
				build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debug/R.jar
				slf4j-api-1.7.25.jar

		[compileDebugUnitTestJavaWithJavac] -----

			Sources
				src/test/java                                                                    NOT EXISTS
				src/testDebug/java                                                               NOT EXISTS

			Output
				build/generated/ap_generated_sources/debugUnitTest/out
				build/intermediates/javac/debugUnitTest/classes
				build/tmp/compileDebugUnitTestJavaWithJavac/previous-compilation-data.bin

			Classpath
				build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debug/R.jar
				build/intermediates/compile_app_classes_jar/debug/classes.jar
				slf4j-api-1.7.25.jar

		[compileReleaseJavaWithJavac] -----

			Sources
				build/generated/aidl_source_output_dir/release/out                               NOT EXISTS
				build/generated/renderscript_source_output_dir/release/out                       NOT EXISTS
				build/generated/source/buildConfig/release                                       NOT EXISTS
				src/main/java
				src/release/java                                                                 NOT EXISTS

			Output
				build/generated/ap_generated_sources/release/out
				build/intermediates/javac/release/classes
				build/tmp/compileReleaseJavaWithJavac/previous-compilation-data.bin

			Classpath
				build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/release/R.jar
				slf4j-api-1.7.25.jar

		[compileReleaseUnitTestJavaWithJavac] -----

			Sources
				src/test/java                                                                    NOT EXISTS
				src/testRelease/java                                                             NOT EXISTS

			Output
				build/generated/ap_generated_sources/releaseUnitTest/out
				build/intermediates/javac/releaseUnitTest/classes
				build/tmp/compileReleaseUnitTestJavaWithJavac/previous-compilation-data.bin

			Classpath
				build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/release/R.jar
				build/intermediates/compile_app_classes_jar/release/classes.jar
				slf4j-api-1.7.25.jar


== [SourceSets] ===============================================================

	Android application Source Sets ------------------------------------------------------ (12)

		androidTest -----
			src/androidTest/java                                                             NOT EXISTS
			src/androidTest/kotlin                                                           NOT EXISTS

		androidTestDebug -----
			src/androidTestDebug/java                                                        NOT EXISTS
			src/androidTestDebug/kotlin                                                      NOT EXISTS

		androidTestRelease -----
			src/androidTestRelease/java                                                      NOT EXISTS
			src/androidTestRelease/kotlin                                                    NOT EXISTS

		debug ----- (consumed by variant debug)
			src/debug/java                                                                   NOT EXISTS
			src/debug/kotlin                                                                 NOT EXISTS

		main ----- (consumed by variant release)

			Sources
				src/main/java
				src/main/kotlin                                                                  NOT EXISTS

			Classpath
				slf4j-api-1.7.25.jar

		release ----- (consumed by variant release)
			src/release/java                                                                 NOT EXISTS
			src/release/kotlin                                                               NOT EXISTS

		test -----
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS

		testDebug -----
			src/testDebug/java                                                               NOT EXISTS
			src/testDebug/kotlin                                                             NOT EXISTS

		testFixtures -----
			src/testFixtures/java                                                            NOT EXISTS
			src/testFixtures/kotlin                                                          NOT EXISTS

		testFixturesDebug -----
			src/testFixturesDebug/java                                                       NOT EXISTS
			src/testFixturesDebug/kotlin                                                     NOT EXISTS

		testFixturesRelease -----
			src/testFixturesRelease/java                                                     NOT EXISTS
			src/testFixturesRelease/kotlin                                                   NOT EXISTS

		testRelease -----
			src/testRelease/java                                                             NOT EXISTS
			src/testRelease/kotlin                                                           NOT EXISTS

== [Android Variants] ========================================================== (2)

	debug ===== (compiled by compileDebugJavaWithJavac task)

		Source sets (2)

			main -----
				src/main/java
				src/main/kotlin                                                                  NOT EXISTS

			debug -----
				src/debug/java                                                                   NOT EXISTS
				src/debug/kotlin                                                                 NOT EXISTS

		Output
			build/intermediates/javac/debug/classes

		Classpath
			build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debug/R.jar
			slf4j-api-1.7.25.jar

	release ===== (compiled by compileReleaseJavaWithJavac task)

		Source sets (2)

			main -----
				src/main/java
				src/main/kotlin                                                                  NOT EXISTS

			release -----
				src/release/java                                                                 NOT EXISTS
				src/release/kotlin                                                               NOT EXISTS

		Output
			build/intermediates/javac/release/classes

		Classpath
			build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/release/R.jar
			slf4j-api-1.7.25.jar

\===========================================================================================
```

## Configuration

The report is giant, but you could disable parts of it:

| Property             | Description                                                              | 
|----------------------|--------------------------------------------------------------------------|
| printPlugins         | Print registered plugins                                                 |            |
| printPlugins         | Print compile tasks (with 'compile' and 'classesl in name and tasks tree | 
| printSourceSets      | Print java, android, kotlin source sets                                  | 
| printAndroidVariants | Print android variants                                                   | 
| printKotlinTargets   | Print kotlin multiplatform platforms                                     | 
| printClasspath       | Print classpath in source sets, variants and platforms                   | 

Task configuration example:

```groovy
printAnimalsnifferSourceInfo.with {
    printPlugins = false
    printClasspath = false
    printCompileTasks = false
    printAndroidVariants = true
    printKotlinTargets = false
}
```